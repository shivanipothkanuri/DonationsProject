package org.charityaid.charity_aid.service;

import java.util.List;

import org.charityaid.charity_aid.dto.BulkCampaignUpdateRequest;
import org.charityaid.charity_aid.dto.CampaignRequest;
import org.charityaid.charity_aid.dto.CampaignResponse;
import org.charityaid.charity_aid.entity.AccountStatus;
import org.charityaid.charity_aid.entity.Campaign;
import org.charityaid.charity_aid.entity.CampaignStatus;
import org.charityaid.charity_aid.entity.User;
import org.charityaid.charity_aid.repository.CampaignRepository;
import org.charityaid.charity_aid.repository.DonationRepository;
import org.charityaid.charity_aid.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final UserRepository userRepository;
    private final DonationRepository donationRepository;
    private final AuditService auditService;
    private final NotificationService notificationService;

    public Page<CampaignResponse> getActiveCampaigns(Pageable pageable) {
        return campaignRepository.findByCampaignStatus(CampaignStatus.ACTIVE, pageable)
                .map(CampaignResponse::from);
    }

    public Page<CampaignResponse> getAllCampaigns(Pageable pageable) {
        return campaignRepository.findAll(pageable).map(CampaignResponse::from);
    }

    public CampaignResponse getCampaignById(Integer id) {
        return CampaignResponse.from(findOrThrow(id));
    }

    @Transactional
    public CampaignResponse createCampaign(CampaignRequest request, String creatorEmail) {
        User creator = userRepository.findByEmailAddress(creatorEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        Campaign campaign = Campaign.builder()
                .campaignTitle(request.getCampaignTitle())
                .description(request.getDescription())
                .goalAmount(request.getGoalAmount())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .category(request.getCategory())
                .campaignStatus(CampaignStatus.ACTIVE)
                .createdBy(creator)
                .build();

        CampaignResponse response = CampaignResponse.from(campaignRepository.save(campaign));
        auditService.record("CAMPAIGN", response.getCampaignId(), "CREATE", creatorEmail,
                "Campaign created: " + request.getCampaignTitle());
        return response;
    }

    @Transactional
    public CampaignResponse updateCampaign(Integer id, CampaignRequest request) {
        Campaign campaign = findOrThrow(id);

        campaign.setCampaignTitle(request.getCampaignTitle());
        campaign.setDescription(request.getDescription());
        campaign.setGoalAmount(request.getGoalAmount());
        campaign.setStartDate(request.getStartDate());
        campaign.setEndDate(request.getEndDate());
        campaign.setCategory(request.getCategory());

        CampaignResponse response = CampaignResponse.from(campaignRepository.save(campaign));
        auditService.record("CAMPAIGN", id, "UPDATE", null, "Campaign updated: " + request.getCampaignTitle());
        return response;
    }

    // FR-33: Manual close by staff
    @Transactional
    public CampaignResponse closeCampaign(Integer id) {
        Campaign campaign = findOrThrow(id);

        if (campaign.getCampaignStatus() != CampaignStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only ACTIVE campaigns can be closed");
        }

        campaign.setCampaignStatus(CampaignStatus.CLOSED);
        CampaignResponse response = CampaignResponse.from(campaignRepository.save(campaign));
        auditService.record("CAMPAIGN", id, "UPDATE", null, "Campaign closed");
        return response;
    }

    // FR-34: Delete only if no donations
    @Transactional
    public void deleteCampaign(Integer id) {
        Campaign campaign = findOrThrow(id);
        campaignRepository.delete(campaign);
        auditService.record("CAMPAIGN", id, "DELETE", null, "Campaign deleted: " + campaign.getCampaignTitle());
    }

    @Transactional
    public void sendBulkCampaignUpdate(Integer id, BulkCampaignUpdateRequest request, String senderEmail) {
        Campaign campaign = findOrThrow(id);
        List<User> donors = donationRepository.findDistinctDonorsByCampaignId(id).stream()
                .filter(user -> user.getAccountStatus() == AccountStatus.ACTIVE)
                .toList();

        for (User donor : donors) {
            notificationService.sendBulkCampaignUpdate(
                    donor.getEmailAddress(),
                    donor.getFullName(),
                    campaign.getCampaignTitle(),
                    request.getMessage());
        }

        auditService.record("CAMPAIGN", id, "BULK_EMAIL", senderEmail,
                "Bulk campaign update sent to " + donors.size() + " donor(s)");
    }

    private Campaign findOrThrow(Integer id) {
        return campaignRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Campaign not found"));
    }
}
