package org.charityaid.charity_aid.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.charityaid.charity_aid.entity.AidRequest;
import org.charityaid.charity_aid.entity.AidType;
import org.charityaid.charity_aid.entity.RequestStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AidRequestResponse {

    private Integer requestId;
    private Integer beneficiaryId;
    private String beneficiaryName;
    private AidType aidType;
    private String description;
    private BigDecimal requestedAmount;
    private String requestedItem;
    private String supportingDocumentation;
    private RequestStatus requestStatus;
    private String justificationNotes;
    private LocalDateTime decisionTimestamp;
    private Integer reviewedById;
    private String reviewedByName;
    private Integer submittedById;
    private String submittedByName;
    private LocalDateTime submissionDate;
    private LocalDateTime fulfillmentDate;

    public static AidRequestResponse from(AidRequest r) {
        return AidRequestResponse.builder()
                .requestId(r.getRequestId())
                .beneficiaryId(r.getBeneficiary().getBeneficiaryId())
                .beneficiaryName(r.getBeneficiary().getFullName())
                .aidType(r.getAidType())
                .description(r.getDescription())
                .requestedAmount(r.getRequestedAmount())
                .requestedItem(r.getRequestedItem())
                .supportingDocumentation(r.getSupportingDocumentation())
                .requestStatus(r.getRequestStatus())
                .justificationNotes(r.getJustificationNotes())
                .decisionTimestamp(r.getDecisionTimestamp())
                .reviewedById(r.getReviewedBy() != null ? r.getReviewedBy().getUserId() : null)
                .reviewedByName(r.getReviewedBy() != null ? r.getReviewedBy().getFullName() : null)
                .submittedById(r.getSubmittedBy().getUserId())
                .submittedByName(r.getSubmittedBy().getFullName())
                .submissionDate(r.getSubmissionDate())
                .fulfillmentDate(r.getFulfillmentDate())
                .build();
    }
}
