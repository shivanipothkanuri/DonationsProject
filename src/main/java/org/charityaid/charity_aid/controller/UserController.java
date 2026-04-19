package org.charityaid.charity_aid.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.charityaid.charity_aid.dto.ApiResponse;
import org.charityaid.charity_aid.dto.ChangePasswordRequest;
import org.charityaid.charity_aid.dto.CreateUserRequest;
import org.charityaid.charity_aid.dto.UpdateProfileRequest;
import org.charityaid.charity_aid.dto.UserResponse;
import org.charityaid.charity_aid.entity.UserRole;
import org.charityaid.charity_aid.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // FR-14: Any authenticated user can view their own profile
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getMyProfile(userDetails.getUsername())));
    }

    // FR-15: Any authenticated user can update their own name/phone
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateMyProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.ok("Profile updated",
                userService.updateMyProfile(userDetails.getUsername(), request)));
    }

    // Change own password
    @PutMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        userService.changePassword(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.ok("Password changed successfully", null));
    }

    // FR-34 (admin only): List all users
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getAllUsers(pageable)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getUserById(id)));
    }

    // FR-02: Admin creates a staff/case manager/leadership account
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("User created", userService.createUser(request)));
    }

    // FR-05: Change role
    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<UserResponse>> changeRole(
            @PathVariable Integer id, @RequestParam UserRole role) {
        return ResponseEntity.ok(ApiResponse.ok("Role updated", userService.changeRole(id, role)));
    }

    // FR-06: Deactivate
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<UserResponse>> deactivateUser(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok("User deactivated", userService.deactivateUser(id)));
    }

    // FR-07: Reactivate
    @PatchMapping("/{id}/reactivate")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<UserResponse>> reactivateUser(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok("User reactivated", userService.reactivateUser(id)));
    }

    // FR-08: Suspend
    @PatchMapping("/{id}/suspend")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<UserResponse>> suspendUser(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok("User suspended", userService.suspendUser(id)));
    }

    // FR-37: Unlock after failed login lockout
    @PatchMapping("/{id}/unlock")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<UserResponse>> unlockUser(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok("Account unlocked", userService.unlockUser(id)));
    }
}
