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
class BeneficiaryServiceTest {

    @Mock
    private Runnable mockDependency;

    @Test
    void getAllBeneficiaries_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(BeneficiaryService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("getAllBeneficiaries"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'getAllBeneficiaries' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void getBeneficiaryById_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(BeneficiaryService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("getBeneficiaryById"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'getBeneficiaryById' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void registerBeneficiary_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(BeneficiaryService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("registerBeneficiary"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'registerBeneficiary' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void updateBeneficiary_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(BeneficiaryService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("updateBeneficiary"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'updateBeneficiary' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void deactivateBeneficiary_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(BeneficiaryService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("deactivateBeneficiary"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'deactivateBeneficiary' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

}
