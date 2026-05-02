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
class NotificationTemplateServiceTest {

    @Mock
    private Runnable mockDependency;

    @Test
    void getAllTemplates_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(NotificationTemplateService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("getAllTemplates"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'getAllTemplates' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void getTemplate_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(NotificationTemplateService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("getTemplate"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'getTemplate' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void updateTemplate_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(NotificationTemplateService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("updateTemplate"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'updateTemplate' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void renderSubject_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(NotificationTemplateService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("renderSubject"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'renderSubject' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void renderBody_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(NotificationTemplateService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("renderBody"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'renderBody' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

}
