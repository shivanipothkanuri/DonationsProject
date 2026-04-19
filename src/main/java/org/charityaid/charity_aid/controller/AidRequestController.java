package org.charityaid.charity_aid.controller;

import java.util.List;

import org.charityaid.charity_aid.dto.AidRequestRequest;
import org.charityaid.charity_aid.dto.AidRequestResponse;
import org.charityaid.charity_aid.dto.AidRequestStatusHistoryResponse;
import org.charityaid.charity_aid.dto.ApiResponse;
import org.charityaid.charity_aid.entity.RequestStatus;
import org.charityaid.charity_aid.service.AidRequestService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/aid-requests")
@RequiredArgsConstructor
public class AidRequestController {

    private final AidRequestService aidRequestService;

    // FR-27: Case manager views pending queue
    @GetMapping
    @PreAuthorize("hasAnyRole('CASE_MANAGER','ADMINISTRATOR','STAFF')")
    public ResponseEntity<ApiResponse<Page<AidRequestResponse>>> getRequests(
            @RequestParam(required = false) RequestStatus status,
            @PageableDefault(size = 10) Pageable pageable) {
        if (status != null) {
            return ResponseEntity.ok(ApiResponse.ok(aidRequestService.getRequestsByStatus(status, pageable)));
        }
        return ResponseEntity.ok(ApiResponse.ok(aidRequestService.getAllRequests(pageable)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CASE_MANAGER','ADMINISTRATOR','STAFF')")
    public ResponseEntity<ApiResponse<AidRequestResponse>> getRequest(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok(aidRequestService.getRequestById(id)));
    }

    // FR-21: View all requests for a beneficiary
    @GetMapping("/beneficiary/{beneficiaryId}")
    @PreAuthorize("hasAnyRole('CASE_MANAGER','ADMINISTRATOR','STAFF')")
    public ResponseEntity<ApiResponse<Page<AidRequestResponse>>> getByBeneficiary(
            @PathVariable Integer beneficiaryId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(
                aidRequestService.getRequestsByBeneficiary(beneficiaryId, pageable)));
    }

    // FR-22: Staff submits a request
    @PostMapping
    @PreAuthorize("hasAnyRole('STAFF','ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<AidRequestResponse>> submitRequest(
            @Valid @RequestBody AidRequestRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        AidRequestResponse response = aidRequestService.submitRequest(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Aid request submitted", response));
    }

    // FR-49: Staff updates a PENDING aid request
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF','ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<AidRequestResponse>> updateRequest(
            @PathVariable Integer id,
            @Valid @RequestBody AidRequestRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Aid request updated",
                aidRequestService.updateRequest(id, request)));
    }

    // FR-29 / FR-30: Case manager approves or denies
    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('CASE_MANAGER','ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<AidRequestResponse>> approveRequest(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.ok("Request approved",
                aidRequestService.reviewRequest(id, true, null, userDetails.getUsername())));
    }

    @PatchMapping("/{id}/deny")
    @PreAuthorize("hasAnyRole('CASE_MANAGER','ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<AidRequestResponse>> denyRequest(
            @PathVariable Integer id,
            @RequestParam String justification,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.ok("Request denied",
                aidRequestService.reviewRequest(id, false, justification, userDetails.getUsername())));
    }

    // FR-24: Staff records fulfillment
    @PatchMapping("/{id}/fulfill")
    @PreAuthorize("hasAnyRole('STAFF','ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<AidRequestResponse>> fulfillRequest(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.ok("Request fulfilled",
                aidRequestService.fulfillRequest(id, userDetails.getUsername())));
    }

    // FR-62: Full status history of an aid request (timestamps + actor)
    @GetMapping("/{id}/history")
    @PreAuthorize("hasAnyRole('CASE_MANAGER','ADMINISTRATOR','STAFF')")
    public ResponseEntity<ApiResponse<List<AidRequestStatusHistoryResponse>>> getStatusHistory(
            @PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok(aidRequestService.getStatusHistory(id)));
    }

    // FR-55: Case manager escalates a PENDING request to administrator
    @PatchMapping("/{id}/escalate")
    @PreAuthorize("hasAnyRole('CASE_MANAGER','ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<AidRequestResponse>> escalateRequest(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.ok("Request escalated to administrator",
                aidRequestService.escalateRequest(id, userDetails.getUsername())));
    }
}
