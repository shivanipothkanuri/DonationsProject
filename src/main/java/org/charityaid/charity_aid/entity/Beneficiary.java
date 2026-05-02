package org.charityaid.charity_aid.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tbl_beneficiaries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Beneficiary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BENEFICIARY_ID")
    private Integer beneficiaryId;

    @Column(name = "FULL_NAME", nullable = false, length = 100)
    private String fullName;

    @Column(name = "EMAIL_ADDRESS", length = 150)
    private String emailAddress;

    @Column(name = "PHONE_NUMBER", nullable = false, length = 20)
    private String phoneNumber;

    @Column(name = "RESIDENTIAL_ADDRESS", nullable = false, length = 255)
    private String residentialAddress;

    // FR-44: Service zone / location tag
    @Column(name = "SERVICE_ZONE", length = 100)
    private String serviceZone;

    // FR-43: Household composition
    @Column(name = "HOUSEHOLD_SIZE")
    private Integer householdSize;

    // FR-42: Primary needs category
    @Enumerated(EnumType.STRING)
    @Column(name = "NEEDS_CATEGORY", length = 20)
    private NeedsCategory needsCategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "BENEFICIARY_STATUS", nullable = false, length = 20)
    @Builder.Default
    private BeneficiaryStatus beneficiaryStatus = BeneficiaryStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REGISTERED_BY", nullable = false)
    private User registeredBy;

    @Column(name = "REGISTRATION_DATE", nullable = false, updatable = false)
    private LocalDateTime registrationDate;

    @PrePersist
    protected void onCreate() {
        registrationDate = LocalDateTime.now();
        if (beneficiaryStatus == null) {
            beneficiaryStatus = BeneficiaryStatus.ACTIVE;
        }
    }
}
