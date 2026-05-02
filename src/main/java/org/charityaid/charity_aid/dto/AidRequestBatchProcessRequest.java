package org.charityaid.charity_aid.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AidRequestBatchProcessRequest {

    @NotEmpty(message = "At least one request ID is required")
    private List<Integer> requestIds;

    private boolean approve;

    private String justification;
}
