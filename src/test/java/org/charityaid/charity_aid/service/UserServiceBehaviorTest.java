package org.charityaid.charity_aid.service;

import java.util.Optional;

import org.charityaid.charity_aid.dto.ChangePasswordRequest;
import org.charityaid.charity_aid.dto.CreateUserRequest;
import org.charityaid.charity_aid.dto.UserResponse;
import org.charityaid.charity_aid.entity.AccountStatus;
import org.charityaid.charity_aid.entity.User;
import org.charityaid.charity_aid.entity.UserRole;
import org.charityaid.charity_aid.repository.UserRepository;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class UserServiceBehaviorTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_throwsConflictWhenEmailAlreadyExists() {
        CreateUserRequest request = new CreateUserRequest();
        request.setFullName("Staff User");
        request.setEmail("staff@example.com");
        request.setPassword("password123");
        request.setUserRole(UserRole.STAFF);

        when(userRepository.existsByEmailAddress("staff@example.com")).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> userService.createUser(request));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }

    @Test
    void createUser_savesEncodedPasswordAndRole() {
        CreateUserRequest request = new CreateUserRequest();
        request.setFullName("Staff User");
        request.setEmail("staff@example.com");
        request.setPassword("password123");
        request.setPhoneNumber("+1 555 2000");
        request.setUserRole(UserRole.STAFF);

        User saved = User.builder()
                .userId(7)
                .fullName("Staff User")
                .emailAddress("staff@example.com")
                .passwordHash("encoded")
                .phoneNumber("+1 555 2000")
                .userRole(UserRole.STAFF)
                .accountStatus(AccountStatus.ACTIVE)
                .build();

        when(userRepository.existsByEmailAddress("staff@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(saved);

        UserResponse response = userService.createUser(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        assertEquals("encoded", userCaptor.getValue().getPasswordHash());
        assertEquals(UserRole.STAFF, userCaptor.getValue().getUserRole());
        assertEquals(7, response.getUserId());
    }

    @Test
    void changePassword_throwsBadRequestWhenCurrentPasswordDoesNotMatch() {
        User existing = User.builder()
                .userId(3)
                .emailAddress("user@example.com")
                .passwordHash("existing-hash")
                .accountStatus(AccountStatus.ACTIVE)
                .userRole(UserRole.DONOR)
                .fullName("Donor")
                .build();

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("wrong-pass");
        request.setNewPassword("new-password-123");

        when(userRepository.findByEmailAddress("user@example.com")).thenReturn(Optional.of(existing));
        when(passwordEncoder.matches("wrong-pass", "existing-hash")).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> userService.changePassword("user@example.com", request));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void changePassword_updatesPasswordWhenCurrentPasswordMatches() {
        User existing = User.builder()
                .userId(3)
                .emailAddress("user@example.com")
                .passwordHash("existing-hash")
                .accountStatus(AccountStatus.ACTIVE)
                .userRole(UserRole.DONOR)
                .fullName("Donor")
                .build();

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("correct-pass");
        request.setNewPassword("new-password-123");

        when(userRepository.findByEmailAddress("user@example.com")).thenReturn(Optional.of(existing));
        when(passwordEncoder.matches("correct-pass", "existing-hash")).thenReturn(true);
        when(passwordEncoder.encode("new-password-123")).thenReturn("new-hash");

        userService.changePassword("user@example.com", request);

        assertEquals("new-hash", existing.getPasswordHash());
        verify(userRepository).save(existing);
    }
}
