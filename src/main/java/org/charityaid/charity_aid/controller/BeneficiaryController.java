package org.charityaid.charity_aid.controller;

import org.charityaid.charity_aid.dto.AidRequestResponse;
import org.charityaid.charity_aid.dto.ApiResponse;
import org.charityaid.charity_aid.dto.BeneficiaryRequest;
import org.charityaid.charity_aid.dto.BeneficiaryResponse;
import org.charityaid.charity_aid.service.BeneficiaryService;
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
@RequestMapping("/api/beneficiaries")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('STAFF','ADMINISTRATOR','CASE_MANAGER')")
public class BeneficiaryController {

    private final BeneficiaryService beneficiaryService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<BeneficiaryResponse>>> getBeneficiaries(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(beneficiaryService.getAllBeneficiaries(search, pageable)));
    }

    // FR-21: View beneficiary profile
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BeneficiaryResponse>> getBeneficiary(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok(beneficiaryService.getBeneficiaryById(id)));
    }

    // FR-19: Register beneficiary
    @PostMapping
    @PreAuthorize("hasAnyRole('STAFF','ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<BeneficiaryResponse>> registerBeneficiary(
            @Valid @RequestBody BeneficiaryRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        BeneficiaryResponse response = beneficiaryService.registerBeneficiary(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Beneficiary registered", response));
    }

    // FR-20: Update contact info
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF','ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<BeneficiaryResponse>> updateBeneficiary(
            @PathVariable Integer id, @Valid @RequestBody BeneficiaryRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Beneficiary updated",
                beneficiaryService.updateBeneficiary(id, request)));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<BeneficiaryResponse>> deactivateBeneficiary(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok("Beneficiary deactivated",
                beneficiaryService.deactivateBeneficiary(id)));
    }

    // FR-45: Beneficiary service history
    @GetMapping("/{id}/history")
    public ResponseEntity<ApiResponse<java.util.List<AidRequestResponse>>> getBeneficiaryHistory(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok(beneficiaryService.getBeneficiaryHistory(id)));
    }
}
