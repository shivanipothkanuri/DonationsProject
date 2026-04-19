package org.charityaid.charity_aid.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.charityaid.charity_aid.entity.CampaignCategory;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CampaignRequest {

    @NotBlank(message = "Campaign title is required")
    @Size(max = 150)
    private String campaignTitle;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Goal amount is required")
    @DecimalMin(value = "1.00", message = "Goal amount must be at least 1.00")
    private BigDecimal goalAmount;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    private CampaignCategory category;
}
