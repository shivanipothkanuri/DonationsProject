package org.charityaid.charity_aid.service;

import java.time.LocalDateTime;
import java.util.List;

import org.charityaid.charity_aid.dto.AuditLogResponse;
import org.charityaid.charity_aid.dto.ComplianceReport;
import org.charityaid.charity_aid.entity.AuditLog;
import org.charityaid.charity_aid.repository.AuditLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    /**
     * Record an audit event. Call this after every CREATE / UPDATE / DELETE
     * on key entities (User, Campaign, Donation, Beneficiary, AidRequest, Inventory).
     *
     * @param entityType  e.g. "USER", "CAMPAIGN"
     * @param entityId    primary key of the affected record
     * @param action      "CREATE", "UPDATE", or "DELETE"
     * @param performedBy email address of the acting user (null for system actions)
     * @param details     optional human-readable summary of what changed
     */
    public void record(String entityType, Integer entityId,
                       String action, String performedBy, String details) {
        auditLogRepository.save(AuditLog.builder()
                .entityType(entityType)
                .entityId(entityId)
                .action(action)
                .performedBy(performedBy)
                .details(details)
                .build());
    }

    // FR-90: Admin views full audit log (paginated, optionally filtered)
    public Page<AuditLogResponse> getAuditLog(String entityType, String performedBy, Pageable pageable) {
        boolean hasEntityType = entityType != null && !entityType.isBlank();
        boolean hasPerformedBy = performedBy != null && !performedBy.isBlank();

        if (hasEntityType && hasPerformedBy) {
            return auditLogRepository.findByEntityTypeAndPerformedBy(entityType.toUpperCase(), performedBy, pageable)
                    .map(AuditLogResponse::from);
        }
        if (hasEntityType) {
            return auditLogRepository.findByEntityType(entityType.toUpperCase(), pageable)
                    .map(AuditLogResponse::from);
        }
        if (hasPerformedBy) {
            return auditLogRepository.findByPerformedBy(performedBy, pageable)
                    .map(AuditLogResponse::from);
        }
        return auditLogRepository.findAll(pageable).map(AuditLogResponse::from);
    }

        public ComplianceReport getComplianceReport(LocalDateTime from, LocalDateTime to,
                            String entityType, String performedBy) {
        List<AuditLog> entries = auditLogRepository.findForComplianceReport(
            from,
            to,
            entityType != null && !entityType.isBlank() ? entityType.toUpperCase() : null,
            performedBy != null && !performedBy.isBlank() ? performedBy : null);

        long createEvents = entries.stream().filter(a -> "CREATE".equalsIgnoreCase(a.getAction())).count();
        long updateEvents = entries.stream().filter(a -> "UPDATE".equalsIgnoreCase(a.getAction())).count();
        long deleteEvents = entries.stream().filter(a -> "DELETE".equalsIgnoreCase(a.getAction())).count();
        long securityEvents = entries.stream()
            .filter(a -> {
                String action = a.getAction() != null ? a.getAction().toUpperCase() : "";
                return action.contains("LOGIN")
                    || action.contains("LOGOUT")
                    || action.contains("MFA")
                    || action.contains("REMINDER");
            })
            .count();

        return ComplianceReport.builder()
            .from(from)
            .to(to)
            .totalEvents(entries.size())
            .createEvents(createEvents)
            .updateEvents(updateEvents)
            .deleteEvents(deleteEvents)
            .securityEvents(securityEvents)
            .entries(entries.stream().map(AuditLogResponse::from).toList())
            .build();
        }
}
