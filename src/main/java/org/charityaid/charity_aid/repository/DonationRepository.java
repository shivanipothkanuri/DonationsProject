package org.charityaid.charity_aid.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.charityaid.charity_aid.entity.Donation;
import org.charityaid.charity_aid.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DonationRepository extends JpaRepository<Donation, Integer> {

    Page<Donation> findByDonor_UserId(Integer donorId, Pageable pageable);

    Page<Donation> findByDonor_UserIdAndDonationDateBetween(
            Integer donorId, LocalDateTime from, LocalDateTime to, Pageable pageable);

    Page<Donation> findByDonor_UserIdAndCampaign_CampaignId(
            Integer donorId, Integer campaignId, Pageable pageable);

    // FR-77: total donations per campaign, optionally filtered by date range
    @Query("SELECT d.campaign.campaignId, d.campaign.campaignTitle, " +
           "SUM(CASE WHEN d.donationAmount IS NOT NULL THEN d.donationAmount ELSE 0 END), " +
           "COUNT(d) " +
           "FROM Donation d " +
           "WHERE (:from IS NULL OR d.donationDate >= :from) " +
           "AND (:to IS NULL OR d.donationDate <= :to) " +
           "AND (:donorId IS NULL OR d.donor.userId = :donorId) " +
           "AND (:campaignId IS NULL OR d.campaign.campaignId = :campaignId) " +
           "GROUP BY d.campaign.campaignId, d.campaign.campaignTitle")
    List<Object[]> sumByCampaign(@Param("from") LocalDateTime from,
                                  @Param("to") LocalDateTime to,
                                  @Param("donorId") Integer donorId,
                                  @Param("campaignId") Integer campaignId);

    // FR-77: Grand total for the period
    @Query("SELECT COALESCE(SUM(d.donationAmount), 0) FROM Donation d " +
           "WHERE d.donationType = org.charityaid.charity_aid.entity.DonationType.MONETARY " +
           "AND (:from IS NULL OR d.donationDate >= :from) " +
           "AND (:to IS NULL OR d.donationDate <= :to)")
    BigDecimal totalMonetary(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

        @Query("SELECT DISTINCT d.donor FROM Donation d WHERE d.campaign.campaignId = :campaignId")
        List<User> findDistinctDonorsByCampaignId(@Param("campaignId") Integer campaignId);
}
