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
class CampaignServiceTest {

    @Mock
    private Runnable mockDependency;

    @Test
    void getActiveCampaigns_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(CampaignService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("getActiveCampaigns"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'getActiveCampaigns' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void getAllCampaigns_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(CampaignService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("getAllCampaigns"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'getAllCampaigns' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void getCampaignById_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(CampaignService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("getCampaignById"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'getCampaignById' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void createCampaign_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(CampaignService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("createCampaign"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'createCampaign' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void updateCampaign_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(CampaignService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("updateCampaign"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'updateCampaign' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void closeCampaign_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(CampaignService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("closeCampaign"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'closeCampaign' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void deleteCampaign_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(CampaignService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("deleteCampaign"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'deleteCampaign' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

    @Test
    void sendBulkCampaignUpdate_shouldExistAsPublicMethod() {
        Method method = Arrays.stream(CampaignService.class.getDeclaredMethods())
                .filter(mm -> mm.getName().equals("sendBulkCampaignUpdate"))
                .findFirst()
                .orElse(null);

        assertNotNull(method, "Expected method 'sendBulkCampaignUpdate' to exist");
        assertTrue(Modifier.isPublic(method.getModifiers()), "Method should be public");
    }

}
