package org.charityaid.charity_aid.service;

import org.charityaid.charity_aid.dto.ChangePasswordRequest;
import org.charityaid.charity_aid.dto.CreateUserRequest;
import org.charityaid.charity_aid.dto.UpdateProfileRequest;
import org.charityaid.charity_aid.dto.UserResponse;
import org.charityaid.charity_aid.entity.AccountStatus;
import org.charityaid.charity_aid.entity.User;
import org.charityaid.charity_aid.entity.UserRole;
import org.charityaid.charity_aid.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserResponse::from);
    }

    public UserResponse getUserById(Integer id) {
        return UserResponse.from(findOrThrow(id));
    }

    public UserResponse getMyProfile(String email) {
        return UserResponse.from(userRepository.findByEmailAddress(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")));
    }

    // FR-14 / FR-15: Update own name and phone
    @Transactional
    public UserResponse updateMyProfile(String email, UpdateProfileRequest request) {
        User user = userRepository.findByEmailAddress(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        if (request.getNotificationsEnabled() != null) {
            user.setNotificationsEnabled(request.getNotificationsEnabled());
        }
        UserResponse response = UserResponse.from(userRepository.save(user));
        auditService.record("USER", user.getUserId(), "UPDATE", email, "Profile updated");
        return response;
    }

    // FR-02: Admin creates staff/case manager/leadership account
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmailAddress(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email address is already registered");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .emailAddress(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .userRole(request.getUserRole())
                .accountStatus(AccountStatus.ACTIVE)
                .build();

        UserResponse response = UserResponse.from(userRepository.save(user));
        auditService.record("USER", response.getUserId(), "CREATE", "system",
                "User created with role " + request.getUserRole());
        return response;
    }

    // FR-05: Admin changes role
    @Transactional
    public UserResponse changeRole(Integer id, UserRole newRole) {
        User user = findOrThrow(id);
        user.setUserRole(newRole);
        UserResponse response = UserResponse.from(userRepository.save(user));
        auditService.record("USER", id, "UPDATE", "system", "Role changed to " + newRole);
        return response;
    }

    // FR-06: Deactivate
    @Transactional
    public UserResponse deactivateUser(Integer id) {
        User user = findOrThrow(id);
        user.setAccountStatus(AccountStatus.INACTIVE);
        UserResponse response = UserResponse.from(userRepository.save(user));
        auditService.record("USER", id, "UPDATE", "system", "Account deactivated");
        return response;
    }

    // FR-07: Reactivate
    @Transactional
    public UserResponse reactivateUser(Integer id) {
        User user = findOrThrow(id);
        user.setAccountStatus(AccountStatus.ACTIVE);
        user.setFailedLoginCount(0);
        UserResponse response = UserResponse.from(userRepository.save(user));
        auditService.record("USER", id, "UPDATE", "system", "Account reactivated");
        return response;
    }

    // FR-08: Suspend
    @Transactional
    public UserResponse suspendUser(Integer id) {
        User user = findOrThrow(id);
        user.setAccountStatus(AccountStatus.SUSPENDED);
        UserResponse response = UserResponse.from(userRepository.save(user));
        auditService.record("USER", id, "UPDATE", "system", "Account suspended");
        return response;
    }

    // FR-37: Unlock account locked due to failed logins
    @Transactional
    public UserResponse unlockUser(Integer id) {
        User user = findOrThrow(id);
        user.setFailedLoginCount(0);
        user.setAccountStatus(AccountStatus.ACTIVE);
        UserResponse response = UserResponse.from(userRepository.save(user));
        auditService.record("USER", id, "UPDATE", "system", "Account unlocked");
        return response;
    }

    // Change own password
    @Transactional
    public void changePassword(String email, ChangePasswordRequest request) {
        User user = userRepository.findByEmailAddress(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password is incorrect");
        }
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    // FR-92: Export a user's own data as JSON
    public String exportMyData(String email) {
        User user = userRepository.findByEmailAddress(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return "{" +
                "\"userId\":" + user.getUserId() + "," +
                "\"fullName\":\"" + escape(user.getFullName()) + "\"," +
                "\"emailAddress\":\"" + escape(user.getEmailAddress()) + "\"," +
                "\"phoneNumber\":\"" + escape(user.getPhoneNumber()) + "\"," +
                "\"userRole\":\"" + user.getUserRole().name() + "\"," +
                "\"accountStatus\":\"" + user.getAccountStatus().name() + "\"," +
                "\"createdDate\":\"" + user.getCreatedDate() + "\"," +
                "\"lastLogin\":\"" + user.getLastLogin() + "\"," +
                "\"notificationsEnabled\":" + user.isNotificationsEnabled() +
                "}";
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private User findOrThrow(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
}
