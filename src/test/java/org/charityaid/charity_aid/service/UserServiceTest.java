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
class UserServiceTest {

    @Mock
    private Runnable mockDependency;

    @Test
    void getAllUsers_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(UserService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("getAllUsers"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'getAllUsers' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void getUserById_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(UserService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("getUserById"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'getUserById' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void getMyProfile_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(UserService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("getMyProfile"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'getMyProfile' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void updateMyProfile_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(UserService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("updateMyProfile"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'updateMyProfile' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void createUser_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(UserService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("createUser"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'createUser' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void changeRole_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(UserService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("changeRole"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'changeRole' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void deactivateUser_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(UserService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("deactivateUser"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'deactivateUser' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void reactivateUser_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(UserService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("reactivateUser"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'reactivateUser' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void suspendUser_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(UserService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("suspendUser"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'suspendUser' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void unlockUser_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(UserService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("unlockUser"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'unlockUser' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void changePassword_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(UserService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("changePassword"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'changePassword' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

}
