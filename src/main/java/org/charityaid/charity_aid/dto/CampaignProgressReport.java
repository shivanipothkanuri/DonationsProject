package org.charityaid.charity_aid.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

/**
 * FR-78: Active campaigns report — each campaign's progress toward its goal.
 */
@Getter
@Builder
public class CampaignProgressReport {

    private int totalActiveCampaigns;
    private List<CampaignProgressItem> campaigns;

    @Getter
    @Builder
    public static class CampaignProgressItem {
        private Integer campaignId;
        private String campaignTitle;
        private String category;
        private BigDecimal goalAmount;
        private BigDecimal collectedAmount;
        private double percentComplete;
        private String status;
    }
}
