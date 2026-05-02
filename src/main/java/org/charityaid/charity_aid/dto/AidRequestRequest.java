package org.charityaid.charity_aid.dto;

import java.math.BigDecimal;

import org.charityaid.charity_aid.entity.AidType;
import org.charityaid.charity_aid.entity.RequestPriority;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AidRequestRequest {

    @NotNull(message = "Beneficiary ID is required")
    private Integer beneficiaryId;

    @NotNull(message = "Aid type is required")
    private AidType aidType;

    @NotBlank(message = "Description is required")
    private String description;

    // Required when aidType = MONETARY
    @DecimalMin(value = "0.01", message = "Requested amount must be greater than 0")
    private BigDecimal requestedAmount;

    // Required for in-kind types
    @Size(max = 255)
    private String requestedItem;

    @Size(max = 500, message = "Supporting documentation reference must be at most 500 characters")
    private String supportingDocumentation;

    // FR-47: Priority level (defaults to MEDIUM if not provided)
    private RequestPriority priority;

    // FR-57: Optionally assign a case manager at submission time
    private Integer assignedCaseManagerId;
}
