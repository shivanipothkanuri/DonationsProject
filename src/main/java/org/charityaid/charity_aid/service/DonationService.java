package org.charityaid.charity_aid.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.charityaid.charity_aid.dto.DonationRequest;
import org.charityaid.charity_aid.dto.DonationResponse;
import org.charityaid.charity_aid.dto.DonorSummaryResponse;
import org.charityaid.charity_aid.dto.RecurringDonationRequest;
import org.charityaid.charity_aid.dto.RecurringDonationResponse;
import org.charityaid.charity_aid.dto.UserResponse;
import org.charityaid.charity_aid.entity.AccountStatus;
import org.charityaid.charity_aid.entity.Campaign;
import org.charityaid.charity_aid.entity.CampaignStatus;
import org.charityaid.charity_aid.entity.Donation;
import org.charityaid.charity_aid.entity.DonationType;
import org.charityaid.charity_aid.entity.Inventory;
import org.charityaid.charity_aid.entity.InventoryMovement;
import org.charityaid.charity_aid.entity.InventoryMovementType;
import org.charityaid.charity_aid.entity.PaymentMethod;
import org.charityaid.charity_aid.entity.RecurringDonation;
import org.charityaid.charity_aid.entity.RecurringDonationFrequency;
import org.charityaid.charity_aid.entity.User;
import org.charityaid.charity_aid.entity.UserRole;
import org.charityaid.charity_aid.repository.CampaignRepository;
import org.charityaid.charity_aid.repository.DonationRepository;
import org.charityaid.charity_aid.repository.InventoryMovementRepository;
import org.charityaid.charity_aid.repository.InventoryRepository;
import org.charityaid.charity_aid.repository.RecurringDonationRepository;
import org.charityaid.charity_aid.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
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
public class DonationService {

    private final DonationRepository donationRepository;
    private final CampaignRepository campaignRepository;
    private final UserRepository userRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryMovementRepository inventoryMovementRepository;
    private final RecurringDonationRepository recurringDonationRepository;
    private final AuditService auditService;
    private final NotificationService notificationService;
    private final ReceiptService receiptService;

    @Value("${app.notifications.high-value-donation-threshold:10000}")
    private BigDecimal highValueDonationThreshold;

    // FR-19 / FR-20: Submit monetary or in-kind donation
    @Transactional
    public DonationResponse submitDonation(DonationRequest request, String donorEmail) {
        User donor = userRepository.findByEmailAddress(donorEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        Campaign campaign = campaignRepository.findById(request.getCampaignId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Campaign not found"));

        ensureActiveCampaign(campaign);

        // Validate monetary vs in-kind fields
        if (request.getDonationType() == DonationType.MONETARY) {
            if (request.getDonationAmount() == null || request.getDonationAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Donation amount is required for monetary donations");
            }
        } else {
            if (request.getItemDescription() == null || request.getItemDescription().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Item description is required for in-kind donations");
            }

            if (request.getItemQuantity() == null || request.getItemQuantity() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Item quantity is required for in-kind donations");
            }

            if (!inventoryRepository.existsByItemNameIgnoreCase(request.getItemDescription().trim())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Selected in-kind item is not available. Please choose from the allowed list.");
            }
        }

        String storedItemDescription = request.getItemDescription();
        if (request.getDonationType() == DonationType.IN_KIND) {
            storedItemDescription = request.getItemDescription().trim() + " (Qty: " + request.getItemQuantity() + ")";
        }

                Donation donation = createDonation(
                    donor,
                    campaign,
                    request.getDonationType(),
                    request.getDonationAmount(),
                    storedItemDescription,
                    request.getPaymentMethod(),
                    request.isAnonymous(),
                    request.getTaxReferenceNumber(),
                    donorEmail,
                    true,
                    false);

                // FR-67: Link in-kind donation to inventory movement
                if (request.getDonationType() == DonationType.IN_KIND && request.getItemDescription() != null) {
                    Inventory item = inventoryRepository.findAllByOrderByItemNameAsc().stream()
                            .filter(i -> i.getItemName().equalsIgnoreCase(request.getItemDescription().trim()))
                            .findFirst()
                            .orElse(null);
                    if (item != null) {
                        int qty = request.getItemQuantity() != null ? request.getItemQuantity() : 1;
                        item.setQuantityOnHand(item.getQuantityOnHand() + qty);
                        inventoryRepository.save(item);
                        inventoryMovementRepository.save(InventoryMovement.builder()
                                .inventory(item)
                                .movementType(InventoryMovementType.CHECK_IN)
                                .quantity(qty)
                                .reference("In-kind donation: " + donation.getTransactionId())
                                .donationId(donation.getDonationId())
                                .actor(donor)
                                .build());
                    }
                }

                return DonationResponse.from(donation);
    }

    public List<String> getInKindDonationOptions() {
        return inventoryRepository.findAllByOrderByItemNameAsc().stream()
                .map(Inventory::getItemName)
                .filter(name -> name != null && !name.isBlank())
                .map(String::trim)
                .distinct()
                .toList();
    }

                @Transactional
                public RecurringDonationResponse scheduleRecurringDonation(RecurringDonationRequest request, String donorEmail) {
                User donor = userRepository.findByEmailAddress(donorEmail)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
                Campaign campaign = campaignRepository.findById(request.getCampaignId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Campaign not found"));

                ensureActiveCampaign(campaign);

                RecurringDonation recurringDonation = RecurringDonation.builder()
                    .donor(donor)
                    .campaign(campaign)
                    .donationAmount(request.getDonationAmount())
                    .paymentMethod(request.getPaymentMethod())
                    .frequency(request.getFrequency())
                    .nextRunAt(request.getFirstRunAt())
                    .active(true)
                    .build();

                recurringDonation = recurringDonationRepository.save(recurringDonation);
                auditService.record("RECURRING_DONATION", recurringDonation.getRecurringDonationId(), "CREATE", donorEmail,
                    "Recurring donation scheduled for campaign " + campaign.getCampaignId());

                return RecurringDonationResponse.from(recurringDonation);
                }

                public List<RecurringDonationResponse> getMyRecurringDonations(String donorEmail) {
                User donor = userRepository.findByEmailAddress(donorEmail)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
                return recurringDonationRepository.findByDonor_UserIdOrderByCreatedAtDesc(donor.getUserId()).stream()
                    .map(RecurringDonationResponse::from)
                    .toList();
                }

                @Transactional
                public void cancelRecurringDonation(Integer id, String donorEmail) {
                User donor = userRepository.findByEmailAddress(donorEmail)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
                RecurringDonation recurringDonation = recurringDonationRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recurring donation not found"));

                if (!recurringDonation.getDonor().getUserId().equals(donor.getUserId())) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only cancel your own recurring donations");
                }

                recurringDonation.setActive(false);
                recurringDonation.setCancelledAt(LocalDateTime.now());
                recurringDonationRepository.save(recurringDonation);
                auditService.record("RECURRING_DONATION", id, "CANCEL", donorEmail, "Recurring donation cancelled");
                }

                @Scheduled(fixedDelayString = "${app.recurring-donations.check-ms:3600000}")
                @Transactional
                public void processRecurringDonations() {
                LocalDateTime now = LocalDateTime.now();
                List<RecurringDonation> dueSchedules = recurringDonationRepository.findByActiveTrueAndNextRunAtLessThanEqual(now);

                for (RecurringDonation schedule : dueSchedules) {
                    Campaign campaign = schedule.getCampaign();
                    if (campaign.getCampaignStatus() != CampaignStatus.ACTIVE) {
                    schedule.setActive(false);
                    schedule.setCancelledAt(now);
                    recurringDonationRepository.save(schedule);
                    auditService.record("RECURRING_DONATION", schedule.getRecurringDonationId(), "AUTO_CANCEL", null,
                        "Recurring donation automatically cancelled because campaign is no longer active");
                    continue;
                    }

                    Donation donation = createDonation(
                        schedule.getDonor(),
                        campaign,
                        DonationType.MONETARY,
                        schedule.getDonationAmount(),
                        null,
                        schedule.getPaymentMethod(),
                        false,
                        null,
                        schedule.getDonor().getEmailAddress(),
                        true,
                        true);

                    schedule.setLastProcessedAt(now);
                    schedule.setNextRunAt(nextRunAt(now, schedule.getFrequency()));
                    recurringDonationRepository.save(schedule);
                    auditService.record("RECURRING_DONATION", schedule.getRecurringDonationId(), "PROCESS", null,
                        "Recurring donation processed as donation " + donation.getDonationId());
                }
                }

    // FR-23 / FR-24 / FR-25: Donation history with filters
    public Page<DonationResponse> getDonorHistory(String donorEmail, LocalDateTime from,
                                                   LocalDateTime to, Integer campaignId, Pageable pageable) {
        User donor = userRepository.findByEmailAddress(donorEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        if (from != null && to != null) {
            return donationRepository
                    .findByDonor_UserIdAndDonationDateBetween(donor.getUserId(), from, to, pageable)
                    .map(DonationResponse::from);
        }
        if (campaignId != null) {
            return donationRepository
                    .findByDonor_UserIdAndCampaign_CampaignId(donor.getUserId(), campaignId, pageable)
                    .map(DonationResponse::from);
        }
        return donationRepository.findByDonor_UserId(donor.getUserId(), pageable)
                .map(DonationResponse::from);
    }

    public DonationResponse getDonationById(Integer id) {
        return DonationResponse.from(donationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Donation not found")));
    }

    // FR-38
    public Page<DonationResponse> searchDonations(String donorName, Pageable pageable) {
        if (donorName != null && !donorName.isBlank()) {
            return donationRepository.findByDonor_FullNameContainingIgnoreCase(donorName, pageable)
                    .map(DonationResponse::from);
        }
        return donationRepository.findAll(pageable).map(DonationResponse::from);
    }

    // FR-50
    public UserResponse getDonorProfile(String donorEmail) {
        User donor = userRepository.findByEmailAddress(donorEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return UserResponse.from(donor);
    }

    // FR-51
    public DonorSummaryResponse getDonorSummary(String donorEmail) {
        User donor = userRepository.findByEmailAddress(donorEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return DonorSummaryResponse.builder()
                .donorId(donor.getUserId())
                .donorName(donor.getFullName())
                .donationCount(donationRepository.countByDonor_UserId(donor.getUserId()))
                .campaignsSupported(donationRepository.distinctCampaignCountByDonorId(donor.getUserId()))
                .totalMonetaryDonated(donationRepository.totalMonetaryByDonorId(donor.getUserId()))
                .build();
    }

    // FR-54
    @Transactional
    public DonationResponse applyMatching(Integer donationId) {
        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Donation not found"));
        if (donation.getDonationType() != DonationType.MONETARY || donation.getDonationAmount() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only monetary donations can be matched");
        }
        donation.setMatchingAmount(donation.getDonationAmount());
        donationRepository.save(donation);
        auditService.record("DONATION", donationId, "MATCH", null, "Donation matching applied 1:1");
        return DonationResponse.from(donation);
    }

    // FR-22: Generate PDF receipt bytes for a donation
    @Transactional
    public byte[] generateReceiptBytes(Integer donationId, String requestorEmail) {
        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Donation not found"));

        // Donors can only download their own receipts; admins/staff can download any
        boolean isDonorOwner = donation.getDonor() != null
                && donation.getDonor().getEmailAddress().equalsIgnoreCase(requestorEmail);
        boolean isStaff = !isDonorOwner; // caller's role is enforced at controller level
        if (!isDonorOwner && !isStaff) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        try {
            byte[] pdf = receiptService.generateReceipt(donation);
            if (!donation.isReceiptGenerated()) {
                donation.setReceiptGenerated(true);
                donationRepository.save(donation);
            }
            return pdf;
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to generate receipt: " + e.getMessage());
        }
    }

    private Donation createDonation(User donor, Campaign campaign, DonationType donationType,
                                    BigDecimal donationAmount, String itemDescription,
                                    PaymentMethod paymentMethod, boolean anonymous,
                        String taxReferenceNumber,
                                    String actorEmail,
                                    boolean sendConfirmation, boolean recurring) {
        Donation donation = Donation.builder()
                .donor(donor)
                .campaign(campaign)
                .donationType(donationType)
                .donationAmount(donationAmount)
                .itemDescription(itemDescription)
                .paymentMethod(paymentMethod)
                .anonymous(anonymous)
            .taxReferenceNumber(taxReferenceNumber)
                .build();

        donation = donationRepository.save(donation);
        auditService.record("DONATION", donation.getDonationId(), "CREATE", actorEmail,
                recurring
                        ? "Recurring donation processed for campaign " + campaign.getCampaignId()
                        : "Donation submitted: " + donationType + " to campaign " + campaign.getCampaignId());

        if (sendConfirmation) {
            byte[] receiptPdf = null;
            String receiptFileName = "receipt-" + donation.getTransactionId() + ".pdf";
            try {
                receiptPdf = receiptService.generateReceipt(donation);
                if (!donation.isReceiptGenerated()) {
                    donation.setReceiptGenerated(true);
                    donation = donationRepository.save(donation);
                }
            } catch (IOException ignored) {
                // Keep the donation flow resilient; still send confirmation without attachment.
            }

            if (receiptPdf != null) {
                notificationService.sendDonationConfirmationWithReceipt(
                        donor.getEmailAddress(),
                        donor.getFullName(),
                        campaign.getCampaignTitle(),
                        donation.getTransactionId(),
                        donationType.name(),
                        donationAmount != null ? donationAmount.toPlainString() : null,
                        receiptPdf,
                        receiptFileName);
            } else {
                notificationService.sendDonationConfirmation(
                        donor.getEmailAddress(),
                        donor.getFullName(),
                        campaign.getCampaignTitle(),
                        donation.getTransactionId(),
                        donationType.name(),
                        donationAmount != null ? donationAmount.toPlainString() : null);
            }
        }

        if (donationType == DonationType.MONETARY && donationAmount != null) {
            campaign.setCollectedAmount(campaign.getCollectedAmount().add(donationAmount));

            if (campaign.getCollectedAmount().compareTo(campaign.getGoalAmount()) >= 0) {
                campaign.setCampaignStatus(CampaignStatus.COMPLETED);
            } else {
                BigDecimal ninety = campaign.getGoalAmount().multiply(new BigDecimal("0.90"));
                if (campaign.getCollectedAmount().compareTo(ninety) >= 0 && campaign.getCreatedBy() != null) {
                    String pct = campaign.getCollectedAmount()
                            .multiply(new BigDecimal("100"))
                            .divide(campaign.getGoalAmount(), 0, java.math.RoundingMode.HALF_UP)
                            .toPlainString();
                    notificationService.sendCampaignNinetyPercentAlert(
                            campaign.getCreatedBy().getEmailAddress(),
                            campaign.getCreatedBy().getFullName(),
                            campaign.getCampaignTitle(),
                            pct);
                }
            }

            if (donationAmount.compareTo(highValueDonationThreshold) >= 0) {
                List<User> leaders = userRepository.findByUserRoleAndAccountStatus(UserRole.LEADERSHIP, AccountStatus.ACTIVE);
                for (User leader : leaders) {
                    notificationService.sendHighValueDonationAlert(
                            leader.getEmailAddress(),
                            leader.getFullName(),
                            donor.getFullName(),
                            campaign.getCampaignTitle(),
                            "$" + donationAmount.toPlainString(),
                            "$" + highValueDonationThreshold.toPlainString());
                }
            }

            campaignRepository.save(campaign);
        }

        return donation;
    }

    private void ensureActiveCampaign(Campaign campaign) {
        if (campaign.getCampaignStatus() != CampaignStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Donations can only be submitted to ACTIVE campaigns");
        }
    }

    private LocalDateTime nextRunAt(LocalDateTime currentRunAt, RecurringDonationFrequency frequency) {
        return switch (frequency) {
            case WEEKLY -> currentRunAt.plusWeeks(1);
            case MONTHLY -> currentRunAt.plusMonths(1);
        };
    }
}
