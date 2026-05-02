package org.charityaid.charity_aid.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private Runnable mockDependency;

    @Test
    void register_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(AuthService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("register"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'register' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void isSetupNeeded_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(AuthService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("isSetupNeeded"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'isSetupNeeded' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void setupFirstAdmin_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(AuthService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("setupFirstAdmin"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'setupFirstAdmin' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void login_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(AuthService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("login"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'login' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void recordLogout_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(AuthService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("recordLogout"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'recordLogout' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void setupMfa_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(AuthService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("setupMfa"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'setupMfa' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void confirmMfa_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(AuthService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("confirmMfa"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'confirmMfa' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void verifyMfa_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(AuthService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("verifyMfa"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'verifyMfa' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void disableMfa_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(AuthService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("disableMfa"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'disableMfa' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void forgotPassword_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(AuthService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("forgotPassword"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'forgotPassword' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void resetPassword_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(AuthService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("resetPassword"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'resetPassword' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

}
