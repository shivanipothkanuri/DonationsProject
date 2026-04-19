package org.charityaid.charity_aid.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

/**
 * FR-79: Pending aid requests report.
 * FR-80: Fulfilled aid report.
 */
@Getter
@Builder
public class AidRequestReport {

    private String reportType;   // "PENDING" or "FULFILLED"
    private long totalCount;
    private List<AidRequestReportItem> requests;

    @Getter
    @Builder
    public static class AidRequestReportItem {
        private Integer requestId;
        private String beneficiaryName;
        private String aidType;
        private String status;
        private BigDecimal requestedAmount;
        private String requestedItem;
        private LocalDateTime submissionDate;
        private LocalDateTime decisionTimestamp;
        private LocalDateTime fulfillmentDate;
        private String reviewedBy;
        private String fulfilledBy;
    }
}
