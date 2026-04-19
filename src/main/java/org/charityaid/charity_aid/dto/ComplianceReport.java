package org.charityaid.charity_aid.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ComplianceReport {

    private LocalDateTime from;
    private LocalDateTime to;
    private long totalEvents;
    private long createEvents;
    private long updateEvents;
    private long deleteEvents;
    private long securityEvents;
    private List<AuditLogResponse> entries;
}