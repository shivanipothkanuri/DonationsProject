package org.charityaid.charity_aid.repository;

import java.util.List;

import org.charityaid.charity_aid.entity.Campaign;
import org.charityaid.charity_aid.entity.CampaignCategory;
import org.charityaid.charity_aid.entity.CampaignStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignRepository extends JpaRepository<Campaign, Integer> {

    Page<Campaign> findByCampaignStatus(CampaignStatus status, Pageable pageable);

    Page<Campaign> findByCampaignStatusAndCategory(CampaignStatus status, CampaignCategory category, Pageable pageable);

    List<Campaign> findByCampaignStatus(CampaignStatus status);

    long countByCampaignStatus(CampaignStatus status);
}
