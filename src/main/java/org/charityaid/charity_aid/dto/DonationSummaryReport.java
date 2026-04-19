package org.charityaid.charity_aid.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

/**
 * FR-77: Donation summary report — total donations by campaign and period.
 */
@Getter
@Builder
public class DonationSummaryReport {

    private LocalDateTime from;
    private LocalDateTime to;
    private BigDecimal grandTotalMonetary;
    private long totalDonationCount;
    private List<CampaignDonationSummary> byCampaign;

    @Getter
    @Builder
    public static class CampaignDonationSummary {
        private Integer campaignId;
        private String campaignTitle;
        private BigDecimal totalAmount;
        private long donationCount;
    }
}
