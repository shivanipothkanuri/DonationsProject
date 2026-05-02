package org.charityaid.charity_aid.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.charityaid.charity_aid.dto.ApiResponse;
import org.charityaid.charity_aid.repository.UserRepository;
import org.charityaid.charity_aid.service.AppConfigService;
import org.charityaid.charity_aid.service.CampaignService;
import org.charityaid.charity_aid.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/system")
@RequiredArgsConstructor
public class SystemController {

    private final AppConfigService appConfigService;
    private final UserRepository userRepository;
    private final InventoryService inventoryService;
    private final CampaignService campaignService;

    // FR-89
    @GetMapping("/health")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','LEADERSHIP')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> health() {
        Map<String, Object> payload = Map.of(
                "status", "UP",
                "timestamp", LocalDateTime.now(),
                "activeUsers", userRepository.countByAccountStatus(org.charityaid.charity_aid.entity.AccountStatus.ACTIVE),
                "inventoryLowStockCount", inventoryService.getLowStockItems().size(),
                "activeCampaigns", campaignService.getActiveCampaigns(org.springframework.data.domain.PageRequest.of(0, 1)).getTotalElements());
        return ResponseEntity.ok(ApiResponse.ok(payload));
    }

    // FR-94
    @GetMapping("/config")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<Map<String, String>>> getConfig() {
        return ResponseEntity.ok(ApiResponse.ok(appConfigService.getSystemConfig()));
    }

    @PatchMapping("/config")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<Map<String, String>>> updateConfig(@RequestBody Map<String, String> updates) {
        return ResponseEntity.ok(ApiResponse.ok("System config updated", appConfigService.updateSystemConfig(updates)));
    }

    // FR-91
    @GetMapping("/permissions")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<Map<String, List<String>>>> getPermissions() {
        return ResponseEntity.ok(ApiResponse.ok(appConfigService.getRolePermissions()));
    }

    @PatchMapping("/permissions/{role}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<List<String>>> updatePermissions(
            @PathVariable String role,
            @RequestBody List<String> permissions) {
        return ResponseEntity.ok(ApiResponse.ok("Permissions updated", appConfigService.updateRolePermissions(role.toUpperCase(), permissions)));
    }
}
