package org.charityaid.charity_aid.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DonorSummaryResponse {

    private Integer donorId;
    private String donorName;
    private BigDecimal totalMonetaryDonated;
    private long donationCount;
    private long campaignsSupported;
}
