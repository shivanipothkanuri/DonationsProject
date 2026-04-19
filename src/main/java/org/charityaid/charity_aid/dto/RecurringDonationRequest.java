package org.charityaid.charity_aid.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.charityaid.charity_aid.entity.PaymentMethod;
import org.charityaid.charity_aid.entity.RecurringDonationFrequency;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecurringDonationRequest {

    @NotNull(message = "Campaign ID is required")
    private Integer campaignId;

    @NotNull(message = "Donation amount is required")
    @DecimalMin(value = "0.01", message = "Donation amount must be greater than 0")
    private BigDecimal donationAmount;

    private PaymentMethod paymentMethod;

    @NotNull(message = "Frequency is required")
    private RecurringDonationFrequency frequency;

    @NotNull(message = "First scheduled run date is required")
    @Future(message = "First scheduled run date must be in the future")
    private LocalDateTime firstRunAt;
}