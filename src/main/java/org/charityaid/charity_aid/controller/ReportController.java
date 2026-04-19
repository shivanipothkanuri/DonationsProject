package org.charityaid.charity_aid.controller;

import java.time.LocalDateTime;

import org.charityaid.charity_aid.dto.AidRequestReport;
import org.charityaid.charity_aid.dto.ApiResponse;
import org.charityaid.charity_aid.dto.CampaignProgressReport;
import org.charityaid.charity_aid.dto.ComplianceReport;
import org.charityaid.charity_aid.dto.DonationSummaryReport;
import org.charityaid.charity_aid.dto.KpiDashboardReport;
import org.charityaid.charity_aid.service.AuditService;
import org.charityaid.charity_aid.service.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final AuditService auditService;

    /**
     * FR-77 + FR-83 + FR-84 + FR-85:
     * Donation summary with optional date-range, donor, and campaign filters.
     * GET /api/reports/donations?from=&to=&donorId=&campaignId=
     */
    @GetMapping("/donations")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','LEADERSHIP','STAFF')")
    public ResponseEntity<ApiResponse<DonationSummaryReport>> donationSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) Integer donorId,
            @RequestParam(required = false) Integer campaignId) {
        return ResponseEntity.ok(ApiResponse.ok(reportService.donationSummary(from, to, donorId, campaignId)));
    }

    /**
     * FR-78: Active campaigns progress toward goal.
     * GET /api/reports/campaigns
     */
    @GetMapping("/campaigns")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','LEADERSHIP','STAFF','CASE_MANAGER')")
    public ResponseEntity<ApiResponse<CampaignProgressReport>> campaignProgress() {
        return ResponseEntity.ok(ApiResponse.ok(reportService.campaignProgress()));
    }

    /**
     * FR-79: Pending aid requests report.
     * GET /api/reports/aid-requests/pending
     */
    @GetMapping("/aid-requests/pending")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','LEADERSHIP','CASE_MANAGER','STAFF')")
    public ResponseEntity<ApiResponse<AidRequestReport>> pendingRequests() {
        return ResponseEntity.ok(ApiResponse.ok(reportService.pendingRequestsReport()));
    }

    /**
     * FR-80: Fulfilled aid report.
     * GET /api/reports/aid-requests/fulfilled
     */
    @GetMapping("/aid-requests/fulfilled")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','LEADERSHIP','CASE_MANAGER','STAFF')")
    public ResponseEntity<ApiResponse<AidRequestReport>> fulfilledRequests() {
        return ResponseEntity.ok(ApiResponse.ok(reportService.fulfilledRequestsReport()));
    }

    /**
     * FR-82 + FR-87: KPI dashboard — Leadership and Admin only.
     * GET /api/reports/kpi
     */
    @GetMapping("/kpi")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','LEADERSHIP')")
    public ResponseEntity<ApiResponse<KpiDashboardReport>> kpiDashboard() {
        return ResponseEntity.ok(ApiResponse.ok(reportService.kpiDashboard()));
    }

    /**
     * FR-86: Export donation summary as CSV download.
     * GET /api/reports/donations/csv?from=&to=&donorId=&campaignId=
     */
    @GetMapping("/donations/csv")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','LEADERSHIP','STAFF')")
    public ResponseEntity<byte[]> donationSummaryCsv(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) Integer donorId,
            @RequestParam(required = false) Integer campaignId) {

        byte[] csv = reportService.donationSummaryCsv(from, to, donorId, campaignId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename("donation-summary.csv")
                        .build());
        return ResponseEntity.ok().headers(headers).body(csv);
    }

    @GetMapping("/compliance")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','LEADERSHIP')")
    public ResponseEntity<ApiResponse<ComplianceReport>> complianceReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) String performedBy) {
        return ResponseEntity.ok(ApiResponse.ok(auditService.getComplianceReport(from, to, entityType, performedBy)));
    }
}
