package org.charityaid.charity_aid.service;

import java.time.LocalDateTime;

import org.charityaid.charity_aid.dto.AuthResponse;
import org.charityaid.charity_aid.dto.LoginRequest;
import org.charityaid.charity_aid.dto.MfaSetupResponse;
import org.charityaid.charity_aid.dto.RegisterRequest;
import org.charityaid.charity_aid.entity.AccountStatus;
import org.charityaid.charity_aid.entity.PasswordResetToken;
import org.charityaid.charity_aid.entity.User;
import org.charityaid.charity_aid.entity.UserRole;
import org.charityaid.charity_aid.repository.PasswordResetTokenRepository;
import org.charityaid.charity_aid.repository.UserRepository;
import org.charityaid.charity_aid.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class AuthService {

    private static final int MAX_FAILED_ATTEMPTS = 5;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final PasswordResetTokenRepository resetTokenRepository;
    private final NotificationService notificationService;
    private final AuditService auditService;
    private final MfaService mfaService;

    @Value("${app.jwt.expiration-ms}")
    private long expirationMs;

    @Value("${app.base-url:http://localhost:8080}")
    private String appBaseUrl;

    // FR-01: Self-registration for donors
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmailAddress(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email address is already registered");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .emailAddress(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .userRole(UserRole.DONOR)
                .accountStatus(AccountStatus.ACTIVE)
                .build();

        user = userRepository.save(user);

        if (user.isNotificationsEnabled()) {
            notificationService.sendSystemNotification(
                user.getEmailAddress(),
                "Welcome to Charity-AID",
                "Your donor account has been created successfully.");
        }

        String token = jwtUtil.generateToken(user.getEmailAddress(), user.getUserRole().name());
        return buildAuthResponse(user, token);
    }

    // FR-02: First-time admin bootstrap — only works when no ADMINISTRATOR account exists
    public boolean isSetupNeeded() {
        return !userRepository.existsByUserRole(UserRole.ADMINISTRATOR);
    }

    @Transactional
    public AuthResponse setupFirstAdmin(RegisterRequest request) {
        if (userRepository.existsByUserRole(UserRole.ADMINISTRATOR)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "An administrator account already exists. Use the login page.");
        }
        if (userRepository.existsByEmailAddress(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email address is already registered");
        }

        User admin = User.builder()
                .fullName(request.getFullName())
                .emailAddress(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .userRole(UserRole.ADMINISTRATOR)
                .accountStatus(AccountStatus.ACTIVE)
                .build();

        admin = userRepository.save(admin);

        if (admin.isNotificationsEnabled()) {
            notificationService.sendSystemNotification(
                admin.getEmailAddress(),
                "Administrator account created",
                "Your administrator account has been set up successfully.");
        }

        String token = jwtUtil.generateToken(admin.getEmailAddress(), admin.getUserRole().name());
        return buildAuthResponse(admin, token);
    }

    // FR-03: Authenticate with email + password. Enforces FR-09 (lock after 5 failures).
    @Transactional(noRollbackFor = ResponseStatusException.class)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmailAddress(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        // Handle explicitly locked accounts
        if (user.getAccountStatus() == AccountStatus.LOCKED) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Account is locked due to too many failed login attempts. Contact an administrator.");
        }

        if (user.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Account is " + user.getAccountStatus().name().toLowerCase() + ". Contact an administrator.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            int newCount = user.getFailedLoginCount() + 1;
            user.setFailedLoginCount(newCount);
            if (newCount >= MAX_FAILED_ATTEMPTS) {
                user.setAccountStatus(AccountStatus.LOCKED);
            }
            userRepository.saveAndFlush(user);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    newCount >= MAX_FAILED_ATTEMPTS
                            ? "Account locked after " + MAX_FAILED_ATTEMPTS + " failed attempts. Contact an administrator."
                            : "Invalid email or password. " + (MAX_FAILED_ATTEMPTS - newCount) + " attempt(s) remaining.");
        }

        // Successful login — reset failed counter, record last login
        user.setFailedLoginCount(0);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        // FR-13: Record login in audit trail
        auditService.record("USER", user.getUserId(), "LOGIN", user.getEmailAddress(), "User logged in");

        // FR-11: If this ADMINISTRATOR has MFA enabled, issue a challenge token instead of a full JWT
        if (user.getUserRole() == UserRole.ADMINISTRATOR && user.isMfaEnabled()) {
            String mfaChallengeToken = jwtUtil.generateMfaChallengeToken(user.getEmailAddress());
            return AuthResponse.builder()
                    .mfaRequired(true)
                    .mfaToken(mfaChallengeToken)
                    .userId(user.getUserId())
                    .fullName(user.getFullName())
                    .email(user.getEmailAddress())
                    .role(user.getUserRole().name())
                    .build();
        }

        String token = jwtUtil.generateToken(user.getEmailAddress(), user.getUserRole().name());
        return buildAuthResponse(user, token);
    }

    private AuthResponse buildAuthResponse(User user, String token) {
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .email(user.getEmailAddress())
                .role(user.getUserRole().name())
                .expiresIn(expirationMs)
                .build();
    }

    // FR-13: Record a logout audit trail entry (called from controller after JWT is validated)
    public void recordLogout(String email) {
        auditService.record("USER", null, "LOGOUT", email, "User logged out");
    }

    // ── FR-11: MFA management ─────────────────────────────────────────────────

    /**
     * Step 1: Administrator initiates MFA setup.
     * Generates a new TOTP secret and stores it (not yet active until confirmed).
     */
    @Transactional
    public MfaSetupResponse setupMfa(String adminEmail) {
        User user = userRepository.findByEmailAddress(adminEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (user.getUserRole() != UserRole.ADMINISTRATOR) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "MFA setup is only available for Administrators");
        }
        String secret = mfaService.generateSecret();
        user.setMfaSecret(secret);
        user.setMfaEnabled(false); // enabled only after confirmation
        userRepository.save(user);
        return MfaSetupResponse.builder()
                .secret(secret)
                .otpauthUrl(mfaService.buildOtpauthUrl(adminEmail, secret))
                .build();
    }

    /**
     * Step 2: Administrator confirms MFA enrollment by submitting the first TOTP code.
     * Only after this call will future logins require MFA.
     */
    @Transactional
    public void confirmMfa(String adminEmail, String totpCode) {
        User user = userRepository.findByEmailAddress(adminEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (user.getMfaSecret() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "MFA setup not initiated. Call POST /api/auth/mfa/setup first.");
        }
        if (!mfaService.verifyCode(user.getMfaSecret(), totpCode)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid TOTP code");
        }
        user.setMfaEnabled(true);
        userRepository.save(user);
        auditService.record("USER", user.getUserId(), "MFA_ENABLE", adminEmail, "MFA enabled");
    }

    /**
     * MFA verification at login: exchange a challenge token + TOTP code for a full JWT.
     */
    public AuthResponse verifyMfa(String mfaToken, String totpCode) {
        if (!jwtUtil.isMfaChallengeToken(mfaToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Invalid or expired MFA challenge token");
        }
        String email = jwtUtil.extractEmail(mfaToken);
        User user = userRepository.findByEmailAddress(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        if (!user.isMfaEnabled() || user.getMfaSecret() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "MFA is not configured for this account");
        }
        if (!mfaService.verifyCode(user.getMfaSecret(), totpCode)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid TOTP code");
        }
        String token = jwtUtil.generateToken(user.getEmailAddress(), user.getUserRole().name());
        
        // FR-13: Record MFA-verified login in audit trail
        auditService.record("USER", user.getUserId(), "LOGIN_MFA_VERIFIED", user.getEmailAddress(), "User logged in with MFA");
        
        return buildAuthResponse(user, token);
    }

    /**
     * Disable MFA for an administrator (clears secret and flag).
     */
    @Transactional
    public void disableMfa(String adminEmail) {
        User user = userRepository.findByEmailAddress(adminEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        user.setMfaEnabled(false);
        user.setMfaSecret(null);
        userRepository.save(user);
        auditService.record("USER", user.getUserId(), "MFA_DISABLE", adminEmail, "MFA disabled");
    }

    // FR-10: Step 1 — request a reset link (always returns 200 to prevent email enumeration)
    @Transactional
    public void forgotPassword(String email) {
        userRepository.findByEmailAddress(email).ifPresent(user -> {
            // Invalidate any existing tokens for this user
            resetTokenRepository.invalidateAllForUser(user.getUserId());

            PasswordResetToken resetToken = PasswordResetToken.builder()
                    .user(user)
                    .build();
            resetTokenRepository.save(resetToken);

            notificationService.sendPasswordResetLink(
                    user.getEmailAddress(),
                    user.getFullName(),
                    resetToken.getToken(),
                    appBaseUrl);
        });
    }

    // FR-10: Step 2 — validate token and set new password
    @Transactional
    public void resetPassword(String rawToken, String newPassword) {
        PasswordResetToken resetToken = resetTokenRepository.findByToken(rawToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Invalid or expired reset token"));

        if (resetToken.isUsed()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reset token has already been used");
        }
        if (resetToken.isExpired()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Reset token has expired");
        }

        User user = resetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setFailedLoginCount(0);
        if (user.getAccountStatus() == AccountStatus.LOCKED) {
            user.setAccountStatus(AccountStatus.ACTIVE);
        }
        userRepository.save(user);

        resetToken.setUsed(true);
        resetTokenRepository.save(resetToken);
    }
}
