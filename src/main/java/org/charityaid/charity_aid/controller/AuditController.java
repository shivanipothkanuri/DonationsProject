package org.charityaid.charity_aid.controller;

import org.charityaid.charity_aid.dto.ApiResponse;
import org.charityaid.charity_aid.dto.AuditLogResponse;
import org.charityaid.charity_aid.service.AuditService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    /**
     * FR-90: Admin views the full audit log.
     * Optional query params: entityType (USER/CAMPAIGN/DONATION/BENEFICIARY/AID_REQUEST/INVENTORY)
     *                        performedBy (email address)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<Page<AuditLogResponse>>> getAuditLog(
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) String performedBy,
            @PageableDefault(size = 20, sort = "performedAt") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(
                auditService.getAuditLog(entityType, performedBy, pageable)));
    }
}
