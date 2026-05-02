package org.charityaid.charity_aid.dto;

import java.math.BigDecimal;

import org.charityaid.charity_aid.entity.DonationType;
import org.charityaid.charity_aid.entity.PaymentMethod;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DonationRequest {

    @NotNull(message = "Campaign ID is required")
    private Integer campaignId;

    @NotNull(message = "Donation type is required")
    private DonationType donationType;

    // Required when donationType = MONETARY
    @DecimalMin(value = "0.01", message = "Donation amount must be greater than 0")
    private BigDecimal donationAmount;

    // Required when donationType = IN_KIND
    @Size(max = 255)
    private String itemDescription;

    // Required when donationType = IN_KIND
    @Min(value = 1, message = "Item quantity must be at least 1")
    private Integer itemQuantity;

    private PaymentMethod paymentMethod;

    // FR-53: Allow donor to remain anonymous on leaderboards and receipts
    private boolean anonymous;

    private String taxReferenceNumber;
}
