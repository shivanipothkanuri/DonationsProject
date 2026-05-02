package org.charityaid.charity_aid.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.charityaid.charity_aid.entity.AidRequest;
import org.charityaid.charity_aid.entity.AidType;
import org.charityaid.charity_aid.entity.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AidRequestRepository extends JpaRepository<AidRequest, Integer> {

    Page<AidRequest> findByRequestStatus(RequestStatus status, Pageable pageable);

    Page<AidRequest> findByAidType(AidType aidType, Pageable pageable);

    Page<AidRequest> findByRequestStatusAndAidType(RequestStatus status, AidType aidType, Pageable pageable);

    Page<AidRequest> findByBeneficiary_BeneficiaryId(Integer beneficiaryId, Pageable pageable);

    // Duplicate check: same beneficiary + aid type within 30 days (FR workflow rule)
    List<AidRequest> findByBeneficiary_BeneficiaryIdAndAidTypeAndSubmissionDateAfter(
            Integer beneficiaryId, AidType aidType, LocalDateTime after);

    Page<AidRequest> findByReviewedBy_UserId(Integer caseManagerId, Pageable pageable);

    // FR-74: guard before deleting inventory — check for pending requests using that item
    boolean existsByRequestedItemIgnoreCaseAndRequestStatus(String requestedItem, RequestStatus status);

    // FR-79 / FR-80: count by status for dashboard
    long countByRequestStatus(RequestStatus status);

    // FR-80: fulfilled requests list
    List<AidRequest> findByRequestStatus(RequestStatus status);

    List<AidRequest> findByRequestStatusAndSubmissionDateBefore(RequestStatus status, LocalDateTime before);

    List<AidRequest> findByBeneficiary_BeneficiaryIdOrderBySubmissionDateDesc(Integer beneficiaryId);
}
