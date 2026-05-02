package org.charityaid.charity_aid.service;

import org.charityaid.charity_aid.dto.AuthResponse;
import org.charityaid.charity_aid.dto.RegisterRequest;
import org.charityaid.charity_aid.entity.AccountStatus;
import org.charityaid.charity_aid.entity.User;
import org.charityaid.charity_aid.entity.UserRole;
import org.charityaid.charity_aid.repository.PasswordResetTokenRepository;
import org.charityaid.charity_aid.repository.UserRepository;
import org.charityaid.charity_aid.security.JwtUtil;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
class AuthServiceBehaviorTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordResetTokenRepository resetTokenRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private AuditService auditService;

    @Mock
    private MfaService mfaService;

    @InjectMocks
    private AuthService authService;

    @Test
    void isSetupNeeded_returnsTrueWhenNoAdministratorExists() {
        when(userRepository.existsByUserRole(UserRole.ADMINISTRATOR)).thenReturn(false);

        assertTrue(authService.isSetupNeeded());
    }

    @Test
    void register_throwsConflictWhenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("Donor");
        request.setEmail("donor@example.com");
        request.setPassword("password123");

        when(userRepository.existsByEmailAddress("donor@example.com")).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> authService.register(request));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }

    @Test
    void register_createsDonorAndReturnsTokenPayload() {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("Donor");
        request.setEmail("donor@example.com");
        request.setPassword("password123");
        request.setPhoneNumber("+1 555 1000");

        User savedUser = User.builder()
                .userId(11)
                .fullName("Donor")
                .emailAddress("donor@example.com")
                .passwordHash("encoded")
                .phoneNumber("+1 555 1000")
                .userRole(UserRole.DONOR)
                .accountStatus(AccountStatus.ACTIVE)
                .build();

        when(userRepository.existsByEmailAddress("donor@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtUtil.generateToken("donor@example.com", "DONOR")).thenReturn("jwt-token");

        AuthResponse response = authService.register(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        assertEquals(UserRole.DONOR, userCaptor.getValue().getUserRole());
        assertEquals("jwt-token", response.getToken());
        assertEquals("DONOR", response.getRole());
        assertEquals(11, response.getUserId());
    }
}
