package org.charityaid.charity_aid.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.charityaid.charity_aid.dto.AidRequestBatchProcessRequest;
import org.charityaid.charity_aid.dto.AidRequestCommentRequest;
import org.charityaid.charity_aid.dto.AidRequestCommentResponse;
import org.charityaid.charity_aid.dto.AidRequestRequest;
import org.charityaid.charity_aid.dto.AidRequestResponse;
import org.charityaid.charity_aid.dto.AidRequestStatusHistoryResponse;
import org.charityaid.charity_aid.entity.AidRequest;
import org.charityaid.charity_aid.entity.AidRequestComment;
import org.charityaid.charity_aid.entity.AidRequestStatusHistory;
import org.charityaid.charity_aid.entity.AidType;
import org.charityaid.charity_aid.entity.Beneficiary;
import org.charityaid.charity_aid.entity.Inventory;
import org.charityaid.charity_aid.entity.RequestPriority;
import org.charityaid.charity_aid.entity.RequestStatus;
import org.charityaid.charity_aid.entity.User;
import org.charityaid.charity_aid.entity.UserRole;
import org.charityaid.charity_aid.repository.AidRequestCommentRepository;
import org.charityaid.charity_aid.repository.AidRequestRepository;
import org.charityaid.charity_aid.repository.AidRequestStatusHistoryRepository;
import org.charityaid.charity_aid.repository.BeneficiaryRepository;
import org.charityaid.charity_aid.repository.InventoryRepository;
import org.charityaid.charity_aid.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class AidRequestService {

    private final AidRequestRepository aidRequestRepository;
    private final AidRequestStatusHistoryRepository statusHistoryRepository;
        private final AidRequestCommentRepository aidRequestCommentRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    private final UserRepository userRepository;
    private final InventoryRepository inventoryRepository;
    private final AuditService auditService;
    private final NotificationService notificationService;

        @Value("${app.aid-request.high-value-threshold:5000}")
        private java.math.BigDecimal highValueThreshold;

        @Value("${app.notifications.pending-reminder-hours:48}")
        private long pendingReminderHours;

        @Value("${app.notifications.pending-reminder-repeat-hours:24}")
        private long pendingReminderRepeatHours;

    public Page<AidRequestResponse> getRequestsByStatus(RequestStatus status, Pageable pageable) {
        return aidRequestRepository.findByRequestStatus(status, pageable)
                .map(AidRequestResponse::from);
    }

        // FR-28: Filter queue by status and/or aid type
        public Page<AidRequestResponse> getRequestsFiltered(RequestStatus status, AidType aidType, Pageable pageable) {
                if (status != null && aidType != null) {
                        return aidRequestRepository.findByRequestStatusAndAidType(status, aidType, pageable)
                                        .map(AidRequestResponse::from);
                }
                if (status != null) {
                        return aidRequestRepository.findByRequestStatus(status, pageable)
                                        .map(AidRequestResponse::from);
                }
                return aidRequestRepository.findByAidType(aidType, pageable)
                                .map(AidRequestResponse::from);
        }

    public Page<AidRequestResponse> getAllRequests(Pageable pageable) {
        return aidRequestRepository.findAll(pageable).map(AidRequestResponse::from);
    }

    public Page<AidRequestResponse> getRequestsByBeneficiary(Integer beneficiaryId, Pageable pageable) {
        return aidRequestRepository.findByBeneficiary_BeneficiaryId(beneficiaryId, pageable)
                .map(AidRequestResponse::from);
    }

    public AidRequestResponse getRequestById(Integer id) {
        return AidRequestResponse.from(findOrThrow(id));
    }

    // FR-22 / FR-23: Staff submits a new aid request; enforces 30-day duplicate rule
    @Transactional
    public AidRequestResponse submitRequest(AidRequestRequest request, String staffEmail) {
        User staff = userRepository.findByEmailAddress(staffEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        Beneficiary beneficiary = beneficiaryRepository.findById(request.getBeneficiaryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Beneficiary not found"));

        // Duplicate check: same beneficiary + aid type within 30 days
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<AidRequest> duplicates = aidRequestRepository
                .findByBeneficiary_BeneficiaryIdAndAidTypeAndSubmissionDateAfter(
                        beneficiary.getBeneficiaryId(), request.getAidType(), thirtyDaysAgo);

        if (!duplicates.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "A request of this type already exists for this beneficiary within the last 30 days");
        }

        AidRequest aidRequest = AidRequest.builder()
                .beneficiary(beneficiary)
                .aidType(request.getAidType())
                .description(request.getDescription())
                .requestedAmount(request.getRequestedAmount())
                .requestedItem(request.getRequestedItem())
                .supportingDocumentation(request.getSupportingDocumentation())
                .priority(request.getPriority() != null ? request.getPriority() : RequestPriority.MEDIUM)
                .slaDeadline(LocalDateTime.now().plusHours(72))
                .requestStatus(RequestStatus.PENDING)
                .submittedBy(staff)
                .build();

        // FR-57: Optionally assign a case manager at submission
        if (request.getAssignedCaseManagerId() != null) {
            User caseManager = userRepository.findById(request.getAssignedCaseManagerId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Case manager not found"));
            aidRequest.setAssignedCaseManager(caseManager);
        }

        AidRequest saved = aidRequestRepository.save(aidRequest);

        // Record initial PENDING history entry (actor = submitting staff)
        statusHistoryRepository.save(AidRequestStatusHistory.builder()
                .aidRequest(saved)
                .status(RequestStatus.PENDING)
                .actor(staff)
                .build());

        auditService.record("AID_REQUEST", saved.getRequestId(), "CREATE", staffEmail,
                "Aid request submitted for beneficiary " + beneficiary.getBeneficiaryId());

        return AidRequestResponse.from(saved);
    }
    @Transactional
    public AidRequestResponse updateRequest(Integer id, AidRequestRequest request) {
        AidRequest aidRequest = findOrThrow(id);

        if (aidRequest.getRequestStatus() != RequestStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Only PENDING requests can be updated");
        }

        Beneficiary beneficiary = beneficiaryRepository.findById(request.getBeneficiaryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Beneficiary not found"));

        aidRequest.setBeneficiary(beneficiary);
        aidRequest.setAidType(request.getAidType());
        aidRequest.setDescription(request.getDescription());
        aidRequest.setRequestedAmount(request.getRequestedAmount());
        aidRequest.setRequestedItem(request.getRequestedItem());
                aidRequest.setSupportingDocumentation(request.getSupportingDocumentation());
        if (request.getPriority() != null) {
            aidRequest.setPriority(request.getPriority());
        }
        if (request.getAssignedCaseManagerId() != null) {
            User caseManager = userRepository.findById(request.getAssignedCaseManagerId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Case manager not found"));
            aidRequest.setAssignedCaseManager(caseManager);
        }

        return AidRequestResponse.from(aidRequestRepository.save(aidRequest));
    }

    // FR-29 / FR-30: Case manager approves or denies
    @Transactional
    public AidRequestResponse reviewRequest(Integer id, boolean approve,
                                             String justification, String reviewerEmail) {
        AidRequest aidRequest = findOrThrow(id);

        if (aidRequest.getRequestStatus() != RequestStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Only PENDING requests can be reviewed");
        }

        User reviewer = userRepository.findByEmailAddress(reviewerEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        if (!approve && (justification == null || justification.isBlank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Justification note is required when denying a request");
        }

        if (approve && isHighValueRequest(aidRequest)
                && (aidRequest.getSupportingDocumentation() == null
                || aidRequest.getSupportingDocumentation().isBlank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Supporting documentation is required before approving a high-value aid request");
        }

        aidRequest.setRequestStatus(approve ? RequestStatus.APPROVED : RequestStatus.DENIED);
        aidRequest.setDecisionTimestamp(LocalDateTime.now());
        aidRequest.setReviewedBy(reviewer);
        aidRequest.setJustificationNotes(justification);

        AidRequest saved = aidRequestRepository.save(aidRequest);

        // Record review decision in history
        statusHistoryRepository.save(AidRequestStatusHistory.builder()
                .aidRequest(saved)
                .status(saved.getRequestStatus())
                .actor(reviewer)
                .notes(justification)
                .build());

        auditService.record("AID_REQUEST", id, "UPDATE", reviewerEmail,
                "Request " + (approve ? "approved" : "denied") + (justification != null ? ": " + justification : ""));

        // FR-60 / FR-61: Notify beneficiary by email
        String beneficiaryEmail = saved.getBeneficiary() != null
                ? saved.getBeneficiary().getEmailAddress() : null;
        String beneficiaryName = saved.getBeneficiary() != null
                ? saved.getBeneficiary().getFullName() : "Beneficiary";
        String aidTypeName = saved.getAidType() != null ? saved.getAidType().name() : "";

        if (approve) {
            notificationService.sendAidRequestApproved(
                    beneficiaryEmail, beneficiaryName, id, aidTypeName);
        } else {
            notificationService.sendAidRequestDenied(
                    beneficiaryEmail, beneficiaryName, id, aidTypeName, justification);
        }

        return AidRequestResponse.from(saved);
    }

    // FR-24 / FR-25: Staff records fulfillment; checks inventory for in-kind
    @Transactional
    public AidRequestResponse fulfillRequest(Integer id, String staffEmail) {
        AidRequest aidRequest = findOrThrow(id);

        if (aidRequest.getRequestStatus() != RequestStatus.APPROVED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Only APPROVED requests can be fulfilled");
        }

        User staff = userRepository.findByEmailAddress(staffEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        // For in-kind types, verify inventory availability
        if (aidRequest.getAidType() != AidType.MONETARY && aidRequest.getRequestedItem() != null) {
            List<Inventory> items = inventoryRepository.findAll();
            Inventory match = items.stream()
                    .filter(i -> i.getItemName().equalsIgnoreCase(aidRequest.getRequestedItem()))
                    .findFirst()
                    .orElse(null);

            if (match == null || match.getQuantityOnHand() <= 0) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Insufficient inventory for item: " + aidRequest.getRequestedItem());
            }

            // Decrement inventory
            match.setQuantityOnHand(match.getQuantityOnHand() - 1);
            inventoryRepository.save(match);
        }

        aidRequest.setRequestStatus(RequestStatus.FULFILLED);
        aidRequest.setFulfillmentDate(LocalDateTime.now());
        aidRequest.setFulfilledBy(staff);

        AidRequest saved = aidRequestRepository.save(aidRequest);

        // Record fulfillment in history
        statusHistoryRepository.save(AidRequestStatusHistory.builder()
                .aidRequest(saved)
                .status(RequestStatus.FULFILLED)
                .actor(staff)
                .build());

        auditService.record("AID_REQUEST", id, "UPDATE", staffEmail, "Request fulfilled");

                if (saved.getReviewedBy() != null) {
                        notificationService.sendFulfillmentNoticeToCaseManager(
                                        saved.getReviewedBy().getEmailAddress(),
                                        saved.getReviewedBy().getFullName(),
                                        id,
                                        saved.getBeneficiary() != null ? saved.getBeneficiary().getFullName() : "Beneficiary",
                                        saved.getAidType() != null ? saved.getAidType().name() : "",
                                        staff.getFullName());
                }

        return AidRequestResponse.from(saved);
    }

    // FR-55: Case manager escalates a PENDING request to administrator
    @Transactional
    public AidRequestResponse escalateRequest(Integer id, String caseManagerEmail) {
        AidRequest aidRequest = findOrThrow(id);

        if (aidRequest.getRequestStatus() != RequestStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Only PENDING requests can be escalated");
        }

        User caseManager = userRepository.findByEmailAddress(caseManagerEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        // Record escalation in status history (status remains PENDING)
        statusHistoryRepository.save(AidRequestStatusHistory.builder()
                .aidRequest(aidRequest)
                .status(RequestStatus.PENDING)
                .actor(caseManager)
                .notes("Escalated to administrator by " + caseManager.getFullName())
                .build());

        auditService.record("AID_REQUEST", id, "ESCALATE", caseManagerEmail,
                "Request escalated to administrator");

        // Notify all active admins
        String beneficiaryName = aidRequest.getBeneficiary() != null
                ? aidRequest.getBeneficiary().getFullName() : "Unknown";
        String aidTypeName = aidRequest.getAidType() != null ? aidRequest.getAidType().name() : "";

        userRepository.findByUserRoleAndAccountStatus(UserRole.ADMINISTRATOR, org.charityaid.charity_aid.entity.AccountStatus.ACTIVE).forEach(admin ->
                notificationService.sendEscalationAlert(
                        admin.getEmailAddress(),
                        admin.getFullName(),
                        id,
                        beneficiaryName,
                        aidTypeName,
                        caseManager.getFullName()));

        return AidRequestResponse.from(aidRequest);
    }

    @Scheduled(fixedDelayString = "${app.notifications.pending-reminder-check-ms:3600000}")
    @Transactional
    public void sendPendingApprovalReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime cutoff = now.minusHours(pendingReminderHours);
        LocalDateTime repeatCutoff = now.minusHours(pendingReminderRepeatHours);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        List<AidRequest> staleRequests = aidRequestRepository.findByRequestStatusAndSubmissionDateBefore(
                RequestStatus.PENDING, cutoff);

        List<User> caseManagers = userRepository.findByUserRoleAndAccountStatus(
                UserRole.CASE_MANAGER,
                org.charityaid.charity_aid.entity.AccountStatus.ACTIVE);

        for (AidRequest request : staleRequests) {
            if (request.getLastReminderSentAt() != null && request.getLastReminderSentAt().isAfter(repeatCutoff)) {
                continue;
            }

            String beneficiaryName = request.getBeneficiary() != null
                    ? request.getBeneficiary().getFullName() : "Beneficiary";
            String submissionDate = request.getSubmissionDate() != null
                    ? request.getSubmissionDate().format(formatter) : "Unknown";

            for (User caseManager : caseManagers) {
                notificationService.sendPendingApprovalReminder(
                        caseManager.getEmailAddress(),
                        caseManager.getFullName(),
                        request.getRequestId(),
                        beneficiaryName,
                        submissionDate);
            }

            request.setLastReminderSentAt(now);
            aidRequestRepository.save(request);
            auditService.record("AID_REQUEST", request.getRequestId(), "REMINDER_SENT", null,
                    "Pending approval reminder sent to case managers");
        }
    }

    // FR-62: Retrieve full status history for an aid request
    public List<AidRequestStatusHistoryResponse> getStatusHistory(Integer requestId) {
        findOrThrow(requestId); // validate request exists
        return statusHistoryRepository
                .findByAidRequest_RequestIdOrderByChangedAtAsc(requestId)
                .stream()
                .map(AidRequestStatusHistoryResponse::from)
                .toList();
    }

        // FR-56
        @Transactional
        public AidRequestCommentResponse addComment(Integer requestId, AidRequestCommentRequest request, String authorEmail) {
                AidRequest aidRequest = findOrThrow(requestId);
                User author = userRepository.findByEmailAddress(authorEmail)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
                AidRequestComment comment = aidRequestCommentRepository.save(AidRequestComment.builder()
                                .aidRequest(aidRequest)
                                .author(author)
                                .commentText(request.getCommentText())
                                .build());
                auditService.record("AID_REQUEST", requestId, "COMMENT", authorEmail, "Comment added to aid request");
                return AidRequestCommentResponse.from(comment);
        }

        public List<AidRequestCommentResponse> getComments(Integer requestId) {
                findOrThrow(requestId);
                return aidRequestCommentRepository.findByAidRequest_RequestIdOrderByCreatedAtAsc(requestId)
                                .stream()
                                .map(AidRequestCommentResponse::from)
                                .toList();
        }

        // FR-59
        @Transactional
        public List<AidRequestResponse> batchProcess(AidRequestBatchProcessRequest request, String reviewerEmail) {
                return request.getRequestIds().stream()
                                .map(id -> reviewRequest(id, request.isApprove(), request.getJustification(), reviewerEmail))
                                .toList();
        }

        // FR-48
        @Transactional
        public AidRequestResponse uploadSupportingDocument(Integer id, MultipartFile file) {
                AidRequest aidRequest = findOrThrow(id);
                if (file == null || file.isEmpty()) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is required");
                }
                try {
                        Path dir = Paths.get("uploads", "aid-requests");
                        Files.createDirectories(dir);
                        String fileName = id + "-" + System.currentTimeMillis() + "-" + file.getOriginalFilename();
                        Path target = dir.resolve(fileName);
                        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
                        aidRequest.setSupportingDocumentPath(target.toString().replace('\\', '/'));
                        aidRequestRepository.save(aidRequest);
                        auditService.record("AID_REQUEST", id, "UPLOAD", null, "Supporting document uploaded");
                        return AidRequestResponse.from(aidRequest);
                } catch (Exception ex) {
                        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to store file");
                }
        }

        // FR-58: mark SLA breaches hourly
        @Scheduled(fixedDelayString = "${app.aid-request.sla-check-ms:3600000}")
        @Transactional
        public void markSlaBreaches() {
                LocalDateTime now = LocalDateTime.now();
                aidRequestRepository.findByRequestStatus(RequestStatus.PENDING).forEach(request -> {
                        if (request.getSlaDeadline() != null && request.getSlaDeadline().isBefore(now) && !request.isSlaBreached()) {
                                request.setSlaBreached(true);
                                aidRequestRepository.save(request);
                                auditService.record("AID_REQUEST", request.getRequestId(), "SLA_BREACH", null,
                                                "Aid request exceeded SLA deadline");
                        }
                });
        }

    private AidRequest findOrThrow(Integer id) {
        return aidRequestRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aid request not found"));
    }

        private boolean isHighValueRequest(AidRequest aidRequest) {
                return aidRequest.getAidType() == AidType.MONETARY
                                && aidRequest.getRequestedAmount() != null
                                && aidRequest.getRequestedAmount().compareTo(highValueThreshold) >= 0;
        }
}
