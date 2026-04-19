package org.charityaid.charity_aid.dto;

import java.time.LocalDateTime;

import org.charityaid.charity_aid.entity.AuditLog;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuditLogResponse {

    private Integer auditId;
    private String entityType;
    private Integer entityId;
    private String action;
    private String performedBy;
    private String details;
    private LocalDateTime performedAt;

    public static AuditLogResponse from(AuditLog log) {
        return AuditLogResponse.builder()
                .auditId(log.getAuditId())
                .entityType(log.getEntityType())
                .entityId(log.getEntityId())
                .action(log.getAction())
                .performedBy(log.getPerformedBy())
                .details(log.getDetails())
                .performedAt(log.getPerformedAt())
                .build();
    }
}
