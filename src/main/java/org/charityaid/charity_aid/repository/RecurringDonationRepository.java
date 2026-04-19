package org.charityaid.charity_aid.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.charityaid.charity_aid.entity.RecurringDonation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecurringDonationRepository extends JpaRepository<RecurringDonation, Integer> {

    List<RecurringDonation> findByDonor_UserIdOrderByCreatedAtDesc(Integer donorId);

    List<RecurringDonation> findByActiveTrueAndNextRunAtLessThanEqual(LocalDateTime dueAt);
}