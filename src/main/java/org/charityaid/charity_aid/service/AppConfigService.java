package org.charityaid.charity_aid.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.charityaid.charity_aid.entity.SystemConfigEntry;
import org.charityaid.charity_aid.entity.UserRole;
import org.charityaid.charity_aid.repository.SystemConfigRepository;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class AppConfigService {

    private static final Map<String, String> DEFAULT_CONFIG = Map.of(
            "rateLimit.perMinute", "120",
            "reports.autoExport.enabled", "true",
            "reports.autoExport.cron", "0 0 2 * * *");

    private final SystemConfigRepository systemConfigRepository;
    private final ConcurrentHashMap<String, List<String>> rolePermissions = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        seedDefaultSystemConfig();
        rolePermissions.put(UserRole.DONOR.name(), List.of("DONATE", "VIEW_OWN_DONATIONS"));
        rolePermissions.put(UserRole.STAFF.name(), List.of("MANAGE_BENEFICIARIES", "MANAGE_INVENTORY", "MANAGE_AID_REQUESTS"));
        rolePermissions.put(UserRole.CASE_MANAGER.name(), List.of("REVIEW_AID_REQUESTS", "ESCALATE_AID_REQUESTS"));
        rolePermissions.put(UserRole.LEADERSHIP.name(), List.of("VIEW_REPORTS", "VIEW_KPI"));
        rolePermissions.put(UserRole.ADMINISTRATOR.name(), List.of("ALL"));
    }

    public Map<String, String> getSystemConfig() {
        Map<String, String> merged = new LinkedHashMap<>(DEFAULT_CONFIG);
        systemConfigRepository.findAll()
                .forEach(entry -> merged.put(entry.getConfigKey(), entry.getConfigValue()));
        return Map.copyOf(merged);
    }

    public Map<String, List<String>> getRolePermissions() {
        return Map.copyOf(rolePermissions);
    }

    public Map<String, String> updateSystemConfig(Map<String, String> updates) {
        if (updates != null) {
            updates.forEach((key, value) -> {
                if (key != null && !key.isBlank() && value != null) {
                    systemConfigRepository.save(SystemConfigEntry.builder()
                            .configKey(key)
                            .configValue(value)
                            .build());
                }
            });
        }
        return getSystemConfig();
    }

    public List<String> updateRolePermissions(String role, List<String> permissions) {
        rolePermissions.put(role, permissions == null ? List.of() : permissions);
        return rolePermissions.get(role);
    }

    public int getRateLimitPerMinute() {
        try {
            return Integer.parseInt(getSystemConfig().getOrDefault("rateLimit.perMinute", "120"));
        } catch (NumberFormatException ex) {
            return 120;
        }
    }

    private void seedDefaultSystemConfig() {
        DEFAULT_CONFIG.forEach((key, value) -> {
            if (!systemConfigRepository.existsById(key)) {
                systemConfigRepository.save(SystemConfigEntry.builder()
                        .configKey(key)
                        .configValue(value)
                        .build());
            }
        });
    }
}
