package org.charityaid.charity_aid.controller;

import org.charityaid.charity_aid.dto.ApiResponse;
import org.charityaid.charity_aid.dto.BulkCampaignUpdateRequest;
import org.charityaid.charity_aid.dto.CampaignRequest;
import org.charityaid.charity_aid.dto.CampaignResponse;
import org.charityaid.charity_aid.service.CampaignService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/campaigns")
@RequiredArgsConstructor
public class CampaignController {

    private final CampaignService campaignService;

    // FR-17: Donors browse active campaigns (all authenticated users)
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<Page<CampaignResponse>>> getActiveCampaigns(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(campaignService.getActiveCampaigns(pageable)));
    }

    // FR-18: Staff/Admin see all campaigns
    @GetMapping
    @PreAuthorize("hasAnyRole('STAFF','ADMINISTRATOR','LEADERSHIP','CASE_MANAGER')")
    public ResponseEntity<ApiResponse<Page<CampaignResponse>>> getAllCampaigns(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(campaignService.getAllCampaigns(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CampaignResponse>> getCampaign(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok(campaignService.getCampaignById(id)));
    }

    // FR-30: Staff creates a campaign
    @PostMapping
    @PreAuthorize("hasAnyRole('STAFF','ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<CampaignResponse>> createCampaign(
            @Valid @RequestBody CampaignRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        CampaignResponse response = campaignService.createCampaign(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Campaign created", response));
    }

    // FR-31 / FR-32: Staff updates campaign
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF','ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<CampaignResponse>> updateCampaign(
            @PathVariable Integer id, @Valid @RequestBody CampaignRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Campaign updated", campaignService.updateCampaign(id, request)));
    }

    // FR-33: Manual close
    @PatchMapping("/{id}/close")
    @PreAuthorize("hasAnyRole('STAFF','ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<CampaignResponse>> closeCampaign(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok("Campaign closed", campaignService.closeCampaign(id)));
    }

    // FR-34: Delete if no donations
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<Void>> deleteCampaign(@PathVariable Integer id) {
        campaignService.deleteCampaign(id);
        return ResponseEntity.ok(ApiResponse.ok("Campaign deleted", null));
    }

    @PostMapping("/{id}/bulk-update")
    @PreAuthorize("hasAnyRole('STAFF','ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<Void>> sendBulkUpdate(
            @PathVariable Integer id,
            @Valid @RequestBody BulkCampaignUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        campaignService.sendBulkCampaignUpdate(id, request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Campaign update emails sent", null));
    }
}
