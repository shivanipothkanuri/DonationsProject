package org.charityaid.charity_aid.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.charityaid.charity_aid.dto.ApiResponse;
import org.charityaid.charity_aid.dto.DonationRequest;
import org.charityaid.charity_aid.dto.DonationResponse;
import org.charityaid.charity_aid.dto.RecurringDonationRequest;
import org.charityaid.charity_aid.dto.RecurringDonationResponse;
import org.charityaid.charity_aid.service.DonationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/donations")
@RequiredArgsConstructor
public class DonationController {

    private final DonationService donationService;

    // FR-19 / FR-20: Submit donation
    @PostMapping
    @PreAuthorize("hasRole('DONOR')")
    public ResponseEntity<ApiResponse<DonationResponse>> submitDonation(
            @Valid @RequestBody DonationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        DonationResponse response = donationService.submitDonation(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Donation recorded", response));
    }

    // Donor-friendly list of in-kind item names sourced from inventory
    @GetMapping("/in-kind-options")
    @PreAuthorize("hasRole('DONOR')")
    public ResponseEntity<ApiResponse<List<String>>> getInKindDonationOptions() {
        return ResponseEntity.ok(ApiResponse.ok(donationService.getInKindDonationOptions()));
    }

    @PostMapping("/recurring")
    @PreAuthorize("hasRole('DONOR')")
    public ResponseEntity<ApiResponse<RecurringDonationResponse>> scheduleRecurringDonation(
            @Valid @RequestBody RecurringDonationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        RecurringDonationResponse response = donationService.scheduleRecurringDonation(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Recurring donation scheduled", response));
    }

    @GetMapping("/recurring/my")
    @PreAuthorize("hasRole('DONOR')")
    public ResponseEntity<ApiResponse<List<RecurringDonationResponse>>> getMyRecurringDonations(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.ok(donationService.getMyRecurringDonations(userDetails.getUsername())));
    }

    @DeleteMapping("/recurring/{id}")
    @PreAuthorize("hasRole('DONOR')")
    public ResponseEntity<ApiResponse<Void>> cancelRecurringDonation(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {
        donationService.cancelRecurringDonation(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Recurring donation cancelled", null));
    }

    // FR-23 / FR-24 / FR-25: Donor views their own history with optional filters
    @GetMapping("/my")
    @PreAuthorize("hasRole('DONOR')")
    public ResponseEntity<ApiResponse<Page<DonationResponse>>> getMyDonations(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) Integer campaignId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(
                donationService.getDonorHistory(userDetails.getUsername(), from, to, campaignId, pageable)));
    }

    // Staff / Admin view any donation
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF','ADMINISTRATOR','CASE_MANAGER')")
    public ResponseEntity<ApiResponse<DonationResponse>> getDonation(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok(donationService.getDonationById(id)));
    }

    // FR-22: Download PDF receipt — donor gets own, staff/admin get any
    @GetMapping("/{id}/receipt")
    @PreAuthorize("hasAnyRole('DONOR','STAFF','ADMINISTRATOR')")
    public ResponseEntity<byte[]> downloadReceipt(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {
        byte[] pdf = donationService.generateReceiptBytes(id, userDetails.getUsername());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename("receipt-" + id + ".pdf")
                        .build());
        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }
}
