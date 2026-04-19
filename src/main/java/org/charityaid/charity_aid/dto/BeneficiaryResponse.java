package org.charityaid.charity_aid.dto;

import org.charityaid.charity_aid.entity.Beneficiary;
import org.charityaid.charity_aid.entity.BeneficiaryStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BeneficiaryResponse {

    private Integer beneficiaryId;
    private String fullName;
    private String emailAddress;
    private String phoneNumber;
    private String residentialAddress;
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
                .beneficiaryStatus(b.getBeneficiaryStatus())
                .registeredById(b.getRegisteredBy().getUserId())
                .registeredByName(b.getRegisteredBy().getFullName())
                .registrationDate(b.getRegistrationDate())
                .build();
    }
}
