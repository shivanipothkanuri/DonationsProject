package org.charityaid.charity_aid.dto;

import java.time.LocalDateTime;

import org.charityaid.charity_aid.entity.Beneficiary;
import org.charityaid.charity_aid.entity.BeneficiaryStatus;
import org.charityaid.charity_aid.entity.NeedsCategory;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BeneficiaryResponse {

    private Integer beneficiaryId;
    private String fullName;
    private String emailAddress;
    private String phoneNumber;
    private String residentialAddress;
    private String serviceZone;
    private Integer householdSize;
    private NeedsCategory needsCategory;
    private BeneficiaryStatus beneficiaryStatus;
    private Integer registeredById;
    private String registeredByName;
    private LocalDateTime registrationDate;

    public static BeneficiaryResponse from(Beneficiary b) {
        return BeneficiaryResponse.builder()
                .beneficiaryId(b.getBeneficiaryId())
                .fullName(b.getFullName())
                .emailAddress(b.getEmailAddress())
                .phoneNumber(b.getPhoneNumber())
                .residentialAddress(b.getResidentialAddress())
                .serviceZone(b.getServiceZone())
                .householdSize(b.getHouseholdSize())
                .needsCategory(b.getNeedsCategory())
                .beneficiaryStatus(b.getBeneficiaryStatus())
                .registeredById(b.getRegisteredBy().getUserId())
                .registeredByName(b.getRegisteredBy().getFullName())
                .registrationDate(b.getRegistrationDate())
                .build();
    }
}
