package org.charityaid.charity_aid.repository;

import org.charityaid.charity_aid.entity.Beneficiary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Integer> {

    Page<Beneficiary> findByFullNameContainingIgnoreCase(String name, Pageable pageable);

    boolean existsByFullNameIgnoreCaseAndPhoneNumber(String fullName, String phoneNumber);
}
