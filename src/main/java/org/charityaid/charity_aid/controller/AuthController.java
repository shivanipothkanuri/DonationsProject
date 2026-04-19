package org.charityaid.charity_aid.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.charityaid.charity_aid.dto.ApiResponse;
import org.charityaid.charity_aid.dto.AuthResponse;
import org.charityaid.charity_aid.dto.ForgotPasswordRequest;
import org.charityaid.charity_aid.dto.LoginRequest;
import org.charityaid.charity_aid.dto.MfaConfirmRequest;
import org.charityaid.charity_aid.dto.MfaSetupResponse;
import org.charityaid.charity_aid.dto.MfaVerifyRequest;
import org.charityaid.charity_aid.dto.RegisterRequest;
import org.charityaid.charity_aid.dto.ResetPasswordRequest;
import org.charityaid.charity_aid.service.AuthService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // FR-01: Donor self-registration (public endpoint)
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Registration successful", response));
    }

    // FR-03: User login (public endpoint)
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Login successful", response));
    }

    // FR-02: Check whether first-admin setup is still needed (public endpoint)
    @GetMapping("/setup-needed")
    public ResponseEntity<ApiResponse<Boolean>> isSetupNeeded() {
        return ResponseEntity.ok(ApiResponse.ok("OK", authService.isSetupNeeded()));
    }

    // FR-02: Create the first administrator account (only works while no admin exists)
    @PostMapping("/setup")
    public ResponseEntity<ApiResponse<AuthResponse>> setupFirstAdmin(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.setupFirstAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Administrator account created successfully", response));
    }

    // FR-10: Step 1 — request a password reset link (always returns 200 to prevent email enumeration)
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request.getEmail());
        return ResponseEntity.ok(ApiResponse.ok(
                "If that email address is registered, a reset link has been sent.", null));
    }

    // FR-10: Step 2 — verify token and set new password
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(ApiResponse.ok("Password has been reset successfully.", null));
    }

    // FR-13: Log logout event — requires a valid JWT (not in permit-all list)
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(Authentication authentication) {
        authService.recordLogout(authentication.getName());
        return ResponseEntity.ok(ApiResponse.ok("Logged out successfully.", null));
    }

    // ── FR-11: MFA endpoints ──────────────────────────────────────────────────

    /**
     * Step 1: Initiate MFA setup — generates a TOTP secret and returns the otpauth:// URL.
     * The administrator scans the QR code in their authenticator app, then calls /mfa/confirm.
     */
    @PostMapping("/mfa/setup")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<MfaSetupResponse>> setupMfa(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.ok(
                "Scan the QR code in your authenticator app, then confirm with POST /api/auth/mfa/confirm",
                authService.setupMfa(authentication.getName())));
    }

    /**
     * Step 2: Confirm MFA enrollment. Administrator submits the first TOTP code to activate MFA.
     * After this, all subsequent logins will require a second factor.
     */
    @PostMapping("/mfa/confirm")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<Void>> confirmMfa(
            @Valid @RequestBody MfaConfirmRequest request,
            Authentication authentication) {
        authService.confirmMfa(authentication.getName(), request.getTotpCode());
        return ResponseEntity.ok(ApiResponse.ok("MFA has been enabled for your account.", null));
    }

    /**
     * MFA verification at login time (public — user has no full JWT yet).
     * Trades the short-lived MFA challenge token + TOTP code for a full JWT.
     */
    @PostMapping("/mfa/verify")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyMfa(
            @Valid @RequestBody MfaVerifyRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("MFA verified",
                authService.verifyMfa(request.getMfaToken(), request.getTotpCode())));
    }

    /**
     * Disable MFA for the authenticated administrator.
     */
    @DeleteMapping("/mfa")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<Void>> disableMfa(Authentication authentication) {
        authService.disableMfa(authentication.getName());
        return ResponseEntity.ok(ApiResponse.ok("MFA has been disabled for your account.", null));
    }
}
