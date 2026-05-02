package org.charityaid.charity_aid.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.charityaid.charity_aid.entity.UserRole;
import org.springframework.stereotype.Service;

@Service
public class AppConfigService {

    private final ConcurrentHashMap<String, String> systemConfig = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<String>> rolePermissions = new ConcurrentHashMap<>();

    public AppConfigService() {
        systemConfig.put("rateLimit.perMinute", "120");
        systemConfig.put("reports.autoExport.enabled", "true");
        systemConfig.put("reports.autoExport.cron", "0 0 2 * * *");

        rolePermissions.put(UserRole.DONOR.name(), List.of("DONATE", "VIEW_OWN_DONATIONS"));
        rolePermissions.put(UserRole.STAFF.name(), List.of("MANAGE_BENEFICIARIES", "MANAGE_INVENTORY", "MANAGE_AID_REQUESTS"));
        rolePermissions.put(UserRole.CASE_MANAGER.name(), List.of("REVIEW_AID_REQUESTS", "ESCALATE_AID_REQUESTS"));
        rolePermissions.put(UserRole.LEADERSHIP.name(), List.of("VIEW_REPORTS", "VIEW_KPI"));
        rolePermissions.put(UserRole.ADMINISTRATOR.name(), List.of("ALL"));
    }

    public Map<String, String> getSystemConfig() {
        return Map.copyOf(systemConfig);
    }

    public Map<String, List<String>> getRolePermissions() {
        return Map.copyOf(rolePermissions);
    }

    public Map<String, String> updateSystemConfig(Map<String, String> updates) {
        if (updates != null) {
            systemConfig.putAll(updates);
        }
        return Map.copyOf(systemConfig);
    }

    public List<String> updateRolePermissions(String role, List<String> permissions) {
        rolePermissions.put(role, permissions == null ? List.of() : permissions);
        return rolePermissions.get(role);
    }

    public int getRateLimitPerMinute() {
        try {
            return Integer.parseInt(systemConfig.getOrDefault("rateLimit.perMinute", "120"));
        } catch (NumberFormatException ex) {
            return 120;
        }
    }
}
