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
class ReceiptServiceTest {

    @Mock
    private Runnable mockDependency;

    @Test
    void generateReceipt_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(ReceiptService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("generateReceipt"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'generateReceipt' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

}
