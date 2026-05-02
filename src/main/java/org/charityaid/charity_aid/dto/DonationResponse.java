package org.charityaid.charity_aid.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.charityaid.charity_aid.entity.Donation;
import org.charityaid.charity_aid.entity.DonationType;
import org.charityaid.charity_aid.entity.PaymentMethod;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DonationResponse {

    private Integer donationId;
    private String transactionId;
    private Integer donorId;
    private String donorName;
    private Integer campaignId;
    private String campaignTitle;
    private DonationType donationType;
    private BigDecimal donationAmount;
    private String itemDescription;
    private PaymentMethod paymentMethod;
    private LocalDateTime donationDate;
    private boolean receiptGenerated;
    private boolean anonymous;
    private String taxReferenceNumber;
    private BigDecimal matchingAmount;

    public static DonationResponse from(Donation d) {
        return DonationResponse.builder()
                .donationId(d.getDonationId())
                .transactionId(d.getTransactionId())
                .donorId(d.getDonor().getUserId())
                .donorName(d.isAnonymous() ? "Anonymous" : d.getDonor().getFullName())
                .campaignId(d.getCampaign().getCampaignId())
                .campaignTitle(d.getCampaign().getCampaignTitle())
                .donationType(d.getDonationType())
                .donationAmount(d.getDonationAmount())
                .itemDescription(d.getItemDescription())
                .paymentMethod(d.getPaymentMethod())
                .donationDate(d.getDonationDate())
                .receiptGenerated(d.isReceiptGenerated())
                .anonymous(d.isAnonymous())
                .taxReferenceNumber(d.getTaxReferenceNumber())
                .matchingAmount(d.getMatchingAmount())
                .build();
    }
}
