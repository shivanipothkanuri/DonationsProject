package org.charityaid.charity_aid.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tbl_aid_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AidRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REQUEST_ID")
    private Integer requestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BENEFICIARY_ID", nullable = false)
    private Beneficiary beneficiary;

    @Enumerated(EnumType.STRING)
    @Column(name = "AID_TYPE", nullable = false, length = 30)
    private AidType aidType;

    @Column(name = "DESCRIPTION", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "REQUESTED_AMOUNT", precision = 10, scale = 2)
    private BigDecimal requestedAmount;

    @Column(name = "REQUESTED_ITEM", length = 255)
    private String requestedItem;

    @Column(name = "SUPPORTING_DOCUMENTATION", length = 500)
    private String supportingDocumentation;

    // FR-48: Uploaded supporting file path
    @Column(name = "SUPPORTING_DOCUMENT_PATH", length = 600)
    private String supportingDocumentPath;

    // FR-47: Request triage priority
    @Enumerated(EnumType.STRING)
    @Column(name = "PRIORITY", length = 10)
    @Builder.Default
    private RequestPriority priority = RequestPriority.MEDIUM;

    // FR-57: Assigned case manager (set before review)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ASSIGNED_CASE_MANAGER_ID")
    private User assignedCaseManager;

    @Enumerated(EnumType.STRING)
    @Column(name = "REQUEST_STATUS", nullable = false, length = 20)
    @Builder.Default
    private RequestStatus requestStatus = RequestStatus.PENDING;

    @Column(name = "JUSTIFICATION_NOTES", columnDefinition = "TEXT")
    private String justificationNotes;

    @Column(name = "DECISION_TIMESTAMP")
    private LocalDateTime decisionTimestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REVIEWED_BY")
    private User reviewedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUBMITTED_BY", nullable = false)
    private User submittedBy;

    @Column(name = "SUBMISSION_DATE", nullable = false, updatable = false)
    private LocalDateTime submissionDate;

    @Column(name = "FULFILLMENT_DATE")
    private LocalDateTime fulfillmentDate;

    @Column(name = "LAST_REMINDER_SENT_AT")
    private LocalDateTime lastReminderSentAt;

    // FR-58: SLA deadline and breach flag
    @Column(name = "SLA_DEADLINE")
    private LocalDateTime slaDeadline;

    @Column(name = "SLA_BREACHED", nullable = false)
    @Builder.Default
    private boolean slaBreached = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FULFILLED_BY")
    private User fulfilledBy;

    @PrePersist
    protected void onCreate() {
        submissionDate = LocalDateTime.now();
        if (requestStatus == null) {
            requestStatus = RequestStatus.PENDING;
        }
    }
}
