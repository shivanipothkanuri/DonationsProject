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
class MfaServiceTest {

    @Mock
    private Runnable mockDependency;

    @Test
    void generateSecret_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(MfaService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("generateSecret"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'generateSecret' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void buildOtpauthUrl_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(MfaService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("buildOtpauthUrl"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'buildOtpauthUrl' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void verifyCode_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(MfaService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("verifyCode"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'verifyCode' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

}
