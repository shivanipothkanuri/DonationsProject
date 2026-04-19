package org.charityaid.charity_aid.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BulkCampaignUpdateRequest {

    @NotBlank(message = "Update message is required")
    private String message;
}