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
class AuditServiceTest {

    @Mock
    private Runnable mockDependency;

    @Test
    void record_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(AuditService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("record"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'record' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void getAuditLog_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(AuditService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("getAuditLog"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'getAuditLog' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void getComplianceReport_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(AuditService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("getComplianceReport"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'getComplianceReport' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

}
