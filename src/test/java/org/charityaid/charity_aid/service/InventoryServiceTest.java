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
class InventoryServiceTest {

    @Mock
    private Runnable mockDependency;

    @Test
    void getAllItems_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(InventoryService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("getAllItems"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'getAllItems' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void getItemById_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(InventoryService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("getItemById"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'getItemById' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void getLowStockItems_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(InventoryService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("getLowStockItems"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'getLowStockItems' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void addItem_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(InventoryService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("addItem"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'addItem' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void updateItem_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(InventoryService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("updateItem"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'updateItem' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void deleteItem_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(InventoryService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("deleteItem"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'deleteItem' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

}
