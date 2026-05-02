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
class NotificationServiceTest {

    @Mock
    private Runnable mockDependency;

    @Test
    void sendDonationConfirmation_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(NotificationService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("sendDonationConfirmation"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'sendDonationConfirmation' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void sendDonationConfirmationWithReceipt_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(NotificationService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("sendDonationConfirmationWithReceipt"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'sendDonationConfirmationWithReceipt' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void sendCampaignNinetyPercentAlert_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(NotificationService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("sendCampaignNinetyPercentAlert"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'sendCampaignNinetyPercentAlert' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void sendAidRequestApproved_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(NotificationService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("sendAidRequestApproved"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'sendAidRequestApproved' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void sendAidRequestDenied_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(NotificationService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("sendAidRequestDenied"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'sendAidRequestDenied' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void sendEscalationAlert_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(NotificationService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("sendEscalationAlert"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'sendEscalationAlert' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void sendPasswordResetLink_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(NotificationService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("sendPasswordResetLink"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'sendPasswordResetLink' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void sendFulfillmentNoticeToCaseManager_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(NotificationService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("sendFulfillmentNoticeToCaseManager"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'sendFulfillmentNoticeToCaseManager' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void sendPendingApprovalReminder_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(NotificationService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("sendPendingApprovalReminder"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'sendPendingApprovalReminder' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void sendBulkCampaignUpdate_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(NotificationService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("sendBulkCampaignUpdate"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'sendBulkCampaignUpdate' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void sendHighValueDonationAlert_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(NotificationService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("sendHighValueDonationAlert"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'sendHighValueDonationAlert' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

}
