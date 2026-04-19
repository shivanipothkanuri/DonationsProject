package org.charityaid.charity_aid.dto;

import org.charityaid.charity_aid.entity.Campaign;
import org.charityaid.charity_aid.entity.CampaignCategory;
import org.charityaid.charity_aid.entity.CampaignStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class CampaignResponse {

    private Integer campaignId;
    private String campaignTitle;
    private String description;
    private BigDecimal goalAmount;
    private BigDecimal collectedAmount;
    private double progressPercent;
    private LocalDate startDate;
    private LocalDate endDate;
    private CampaignStatus campaignStatus;
    private CampaignCategory category;
    private Integer createdById;
    private String createdByName;
    private LocalDateTime createdDate;

    public static CampaignResponse from(Campaign c) {
        double progress = c.getGoalAmount().compareTo(BigDecimal.ZERO) > 0
                ? c.getCollectedAmount()
                        .multiply(BigDecimal.valueOf(100))
                        .divide(c.getGoalAmount(), 2, RoundingMode.HALF_UP)
                        .doubleValue()
                : 0.0;

        return CampaignResponse.builder()
                .campaignId(c.getCampaignId())
                .campaignTitle(c.getCampaignTitle())
                .description(c.getDescription())
                .goalAmount(c.getGoalAmount())
                .collectedAmount(c.getCollectedAmount())
                .progressPercent(progress)
                .startDate(c.getStartDate())
                .endDate(c.getEndDate())
                .campaignStatus(c.getCampaignStatus())
                .category(c.getCategory())
                .createdById(c.getCreatedBy().getUserId())
                .createdByName(c.getCreatedBy().getFullName())
                .createdDate(c.getCreatedDate())
                .build();
    }
}
