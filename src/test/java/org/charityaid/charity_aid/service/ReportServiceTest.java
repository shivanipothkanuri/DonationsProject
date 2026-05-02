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
class ReportServiceTest {

    @Mock
    private Runnable mockDependency;

    @Test
    void donationSummary_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(ReportService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("donationSummary"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'donationSummary' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void campaignProgress_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(ReportService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("campaignProgress"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'campaignProgress' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void pendingRequestsReport_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(ReportService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("pendingRequestsReport"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'pendingRequestsReport' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void fulfilledRequestsReport_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(ReportService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("fulfilledRequestsReport"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'fulfilledRequestsReport' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void kpiDashboard_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(ReportService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("kpiDashboard"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'kpiDashboard' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void donationSummaryCsv_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(ReportService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("donationSummaryCsv"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'donationSummaryCsv' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

}
