package org.charityaid.charity_aid.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tbl_audit_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AUDIT_ID")
    private Integer auditId;

    /** e.g. "USER", "CAMPAIGN", "DONATION", "BENEFICIARY", "AID_REQUEST", "INVENTORY" */
    @Column(name = "ENTITY_TYPE", nullable = false, length = 50)
    private String entityType;

    /** The primary key of the affected record */
    @Column(name = "ENTITY_ID")
    private Integer entityId;

    /** CREATE, UPDATE, DELETE */
    @Column(name = "ACTION", nullable = false, length = 20)
    private String action;

    /** email address of the actor (stored as plain string so log survives account deletion) */
    @Column(name = "PERFORMED_BY", length = 150)
    private String performedBy;

    /** Optional human-readable summary of what changed */
    @Column(name = "DETAILS", columnDefinition = "TEXT")
    private String details;

    @Column(name = "PERFORMED_AT", nullable = false, updatable = false)
    private LocalDateTime performedAt;

    @PrePersist
    protected void onCreate() {
        if (performedAt == null) {
            performedAt = LocalDateTime.now();
        }
    }
}
