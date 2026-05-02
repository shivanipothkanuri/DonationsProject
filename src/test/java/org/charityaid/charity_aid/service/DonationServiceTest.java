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
class DonationServiceTest {

    @Mock
    private Runnable mockDependency;

    @Test
    void submitDonation_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(DonationService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("submitDonation"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'submitDonation' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void getInKindDonationOptions_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(DonationService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("getInKindDonationOptions"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'getInKindDonationOptions' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void scheduleRecurringDonation_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(DonationService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("scheduleRecurringDonation"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'scheduleRecurringDonation' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void getMyRecurringDonations_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(DonationService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("getMyRecurringDonations"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'getMyRecurringDonations' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void cancelRecurringDonation_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(DonationService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("cancelRecurringDonation"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'cancelRecurringDonation' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void processRecurringDonations_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(DonationService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("processRecurringDonations"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'processRecurringDonations' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void getDonorHistory_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(DonationService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("getDonorHistory"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'getDonorHistory' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void getDonationById_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(DonationService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("getDonationById"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'getDonationById' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void generateReceiptBytes_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(DonationService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("generateReceiptBytes"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'generateReceiptBytes' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

}
