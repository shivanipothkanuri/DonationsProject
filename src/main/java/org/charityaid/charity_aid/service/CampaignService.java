package org.charityaid.charity_aid.service;

import java.time.LocalDate;
import java.util.List;

import org.charityaid.charity_aid.dto.BulkCampaignUpdateRequest;
import org.charityaid.charity_aid.dto.CampaignRequest;
import org.charityaid.charity_aid.dto.CampaignResponse;
import org.charityaid.charity_aid.entity.AccountStatus;
import org.charityaid.charity_aid.entity.Campaign;
import org.charityaid.charity_aid.entity.CampaignCategory;
import org.charityaid.charity_aid.entity.CampaignStatus;
import org.charityaid.charity_aid.entity.RequestStatus;
import org.charityaid.charity_aid.entity.User;
import org.charityaid.charity_aid.repository.AidRequestRepository;
import org.charityaid.charity_aid.repository.CampaignRepository;
import org.charityaid.charity_aid.repository.DonationRepository;
import org.charityaid.charity_aid.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
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
    private final AidRequestRepository aidRequestRepository;
    private final AuditService auditService;
    private final NotificationService notificationService;

    public Page<CampaignResponse> getActiveCampaigns(Pageable pageable) {
        return campaignRepository.findByCampaignStatus(CampaignStatus.ACTIVE, pageable)
                .map(CampaignResponse::from);
    }

    // FR-36: Filter active campaigns by category
    public Page<CampaignResponse> getActiveCampaignsByCategory(CampaignCategory category, Pageable pageable) {
        return campaignRepository.findByCampaignStatusAndCategory(CampaignStatus.ACTIVE, category, pageable)
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
        if (donationRepository.existsByCampaign_CampaignId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cannot delete campaign with existing donations");
        }
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

    // FR-72: Auto-close campaigns whose endDate has passed — runs daily at midnight
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void autoCloseExpiredCampaigns() {
        List<Campaign> expired = campaignRepository.findByCampaignStatus(CampaignStatus.ACTIVE).stream()
                .filter(c -> c.getEndDate() != null && c.getEndDate().isBefore(LocalDate.now()))
                .toList();
        for (Campaign campaign : expired) {
            campaign.setCampaignStatus(CampaignStatus.CLOSED);
            campaignRepository.save(campaign);
            auditService.record("CAMPAIGN", campaign.getCampaignId(), "AUTO_CLOSE", "SYSTEM",
                    "Campaign auto-closed: end date " + campaign.getEndDate() + " has passed");
        }
    }

    // FR-70: milestone notifications
    @Scheduled(fixedDelayString = "${app.campaign.milestone-check-ms:3600000}")
    @Transactional
    public void processCampaignMilestones() {
        List<Campaign> active = campaignRepository.findByCampaignStatus(CampaignStatus.ACTIVE);
        for (Campaign campaign : active) {
            if (campaign.getGoalAmount() == null || campaign.getGoalAmount().signum() <= 0) {
                continue;
            }
            int pct = campaign.getCollectedAmount().multiply(java.math.BigDecimal.valueOf(100))
                    .divide(campaign.getGoalAmount(), java.math.RoundingMode.HALF_UP)
                    .intValue();
            if (pct >= 25 && !campaign.isMilestone25Sent()) {
                campaign.setMilestone25Sent(true);
                auditService.record("CAMPAIGN", campaign.getCampaignId(), "MILESTONE", "SYSTEM", "25% milestone reached");
            }
            if (pct >= 50 && !campaign.isMilestone50Sent()) {
                campaign.setMilestone50Sent(true);
                auditService.record("CAMPAIGN", campaign.getCampaignId(), "MILESTONE", "SYSTEM", "50% milestone reached");
            }
            if (pct >= 75 && !campaign.isMilestone75Sent()) {
                campaign.setMilestone75Sent(true);
                auditService.record("CAMPAIGN", campaign.getCampaignId(), "MILESTONE", "SYSTEM", "75% milestone reached");
            }
            if (pct >= 100 && !campaign.isMilestone100Sent()) {
                campaign.setMilestone100Sent(true);
                auditService.record("CAMPAIGN", campaign.getCampaignId(), "MILESTONE", "SYSTEM", "100% milestone reached");
            }
            campaignRepository.save(campaign);
        }
    }

    public List<java.util.Map<String, Object>> getCampaignLeaderboard(Integer campaignId) {
        findOrThrow(campaignId);
        return donationRepository.findByCampaign_CampaignId(campaignId).stream()
                .filter(d -> d.getDonationAmount() != null)
                .collect(java.util.stream.Collectors.groupingBy(
                        d -> d.isAnonymous() ? "Anonymous" : d.getDonor().getFullName(),
                        java.util.stream.Collectors.reducing(java.math.BigDecimal.ZERO, org.charityaid.charity_aid.entity.Donation::getDonationAmount, java.math.BigDecimal::add)
                ))
                .entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(10)
                .map(e -> java.util.Map.<String, Object>of("donorName", e.getKey(), "totalAmount", e.getValue()))
                .toList();
    }

    public String getShareLink(Integer campaignId) {
        Campaign campaign = findOrThrow(campaignId);
        return "/campaigns.html?share=" + campaign.getShareSlug();
    }

    public java.util.Map<String, Object> getImpactMetrics(Integer campaignId) {
        findOrThrow(campaignId);
        long fulfilled = aidRequestRepository.countByRequestStatus(RequestStatus.FULFILLED);
        return java.util.Map.of(
                "campaignId", campaignId,
                "beneficiariesImpactedEstimate", fulfilled,
                "note", "Estimate is based on fulfilled aid requests in the system");
    }
}
