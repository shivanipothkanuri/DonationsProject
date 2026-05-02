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
class AidRequestServiceTest {

    @Mock
    private Runnable mockDependency;

    @Test
    void getRequestsByStatus_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(AidRequestService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("getRequestsByStatus"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'getRequestsByStatus' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void getAllRequests_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(AidRequestService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("getAllRequests"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'getAllRequests' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void getRequestsByBeneficiary_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(AidRequestService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("getRequestsByBeneficiary"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'getRequestsByBeneficiary' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void getRequestById_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(AidRequestService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("getRequestById"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'getRequestById' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void submitRequest_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(AidRequestService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("submitRequest"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'submitRequest' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void updateRequest_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(AidRequestService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("updateRequest"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'updateRequest' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void reviewRequest_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(AidRequestService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("reviewRequest"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'reviewRequest' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void fulfillRequest_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(AidRequestService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("fulfillRequest"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'fulfillRequest' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void escalateRequest_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(AidRequestService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("escalateRequest"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'escalateRequest' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void sendPendingApprovalReminders_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(AidRequestService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("sendPendingApprovalReminders"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'sendPendingApprovalReminders' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void getStatusHistory_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(AidRequestService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("getStatusHistory"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'getStatusHistory' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

}
