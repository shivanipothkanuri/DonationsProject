package org.charityaid.charity_aid.dto;

import org.charityaid.charity_aid.entity.NeedsCategory;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BeneficiaryRequest {

    @NotBlank(message = "Full name is required")
    @Size(max = 100)
    private String fullName;

    @Size(max = 150)
    private String emailAddress;

    @NotBlank(message = "Phone number is required")
    @Size(max = 20)
    private String phoneNumber;

    @NotBlank(message = "Residential address is required")
    @Size(max = 255)
    private String residentialAddress;

    @Size(max = 100)
    private String serviceZone;

    // FR-43: Household size
    @Min(value = 1, message = "Household size must be at least 1")
    private Integer householdSize;

    // FR-42: Primary needs category
    private NeedsCategory needsCategory;
}
