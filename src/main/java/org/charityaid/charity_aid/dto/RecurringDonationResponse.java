package org.charityaid.charity_aid.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.charityaid.charity_aid.entity.RecurringDonation;
import org.charityaid.charity_aid.entity.RecurringDonationFrequency;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecurringDonationResponse {

    private Integer recurringDonationId;
    private Integer campaignId;
    private String campaignTitle;
    private BigDecimal donationAmount;
    private RecurringDonationFrequency frequency;
    private LocalDateTime nextRunAt;
    private LocalDateTime lastProcessedAt;
    private boolean active;

    public static RecurringDonationResponse from(RecurringDonation recurringDonation) {
        return RecurringDonationResponse.builder()
                .recurringDonationId(recurringDonation.getRecurringDonationId())
                .campaignId(recurringDonation.getCampaign().getCampaignId())
                .campaignTitle(recurringDonation.getCampaign().getCampaignTitle())
                .donationAmount(recurringDonation.getDonationAmount())
                .frequency(recurringDonation.getFrequency())
                .nextRunAt(recurringDonation.getNextRunAt())
                .lastProcessedAt(recurringDonation.getLastProcessedAt())
                .active(recurringDonation.isActive())
                .build();
    }
}