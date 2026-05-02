package org.charityaid.charity_aid.service;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.charityaid.charity_aid.dto.AidRequestReport;
import org.charityaid.charity_aid.dto.AidRequestReport.AidRequestReportItem;
import org.charityaid.charity_aid.dto.CampaignProgressReport;
import org.charityaid.charity_aid.dto.CampaignProgressReport.CampaignProgressItem;
import org.charityaid.charity_aid.dto.DonationSummaryReport;
import org.charityaid.charity_aid.dto.DonationSummaryReport.CampaignDonationSummary;
import org.charityaid.charity_aid.dto.KpiDashboardReport;
import org.charityaid.charity_aid.entity.AccountStatus;
import org.charityaid.charity_aid.entity.AidRequest;
import org.charityaid.charity_aid.entity.Campaign;
import org.charityaid.charity_aid.entity.CampaignStatus;
import org.charityaid.charity_aid.entity.RequestStatus;
import org.charityaid.charity_aid.repository.AidRequestRepository;
import org.charityaid.charity_aid.repository.CampaignRepository;
import org.charityaid.charity_aid.repository.DonationRepository;
import org.charityaid.charity_aid.repository.InventoryRepository;
import org.charityaid.charity_aid.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final DonationRepository donationRepository;
    private final CampaignRepository campaignRepository;
    private final AidRequestRepository aidRequestRepository;
    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;
        private final AuditService auditService;

    /**
     * FR-77: Donation summary — totals by campaign, optionally filtered by date
     * range, donor, or campaign. Satisfies FR-83, FR-84, FR-85 filters.
     */
    public DonationSummaryReport donationSummary(LocalDateTime from, LocalDateTime to,
                                                  Integer donorId, Integer campaignId) {
        List<Object[]> rows = donationRepository.sumByCampaign(from, to, donorId, campaignId);

        List<CampaignDonationSummary> byCampaign = rows.stream()
                .map(r -> CampaignDonationSummary.builder()
                        .campaignId((Integer) r[0])
                        .campaignTitle((String) r[1])
                        .totalAmount(r[2] != null ? (BigDecimal) r[2] : BigDecimal.ZERO)
                        .donationCount((Long) r[3])
                        .build())
                .collect(Collectors.toList());

        BigDecimal grandTotal = donationRepository.totalMonetary(from, to);
        long totalCount = byCampaign.stream().mapToLong(CampaignDonationSummary::getDonationCount).sum();

        return DonationSummaryReport.builder()
                .from(from)
                .to(to)
                .grandTotalMonetary(grandTotal != null ? grandTotal : BigDecimal.ZERO)
                .totalDonationCount(totalCount)
                .byCampaign(byCampaign)
                .build();
    }

    /**
     * FR-78: Active campaigns progress report.
     */
    public CampaignProgressReport campaignProgress() {
        List<Campaign> active = campaignRepository.findByCampaignStatus(CampaignStatus.ACTIVE);

        List<CampaignProgressItem> items = active.stream()
                .map(c -> {
                    double pct = c.getGoalAmount().compareTo(BigDecimal.ZERO) > 0
                            ? c.getCollectedAmount()
                                    .multiply(new BigDecimal("100"))
                                    .divide(c.getGoalAmount(), 2, java.math.RoundingMode.HALF_UP)
                                    .doubleValue()
                            : 0.0;
                    return CampaignProgressItem.builder()
                            .campaignId(c.getCampaignId())
                            .campaignTitle(c.getCampaignTitle())
                            .category(c.getCategory() != null ? c.getCategory().name() : null)
                            .goalAmount(c.getGoalAmount())
                            .collectedAmount(c.getCollectedAmount())
                            .percentComplete(pct)
                            .status(c.getCampaignStatus().name())
                            .build();
                })
                .collect(Collectors.toList());

        return CampaignProgressReport.builder()
                .totalActiveCampaigns(items.size())
                .campaigns(items)
                .build();
    }

    /**
     * FR-79: Pending aid requests report.
     */
    public AidRequestReport pendingRequestsReport() {
        List<AidRequest> pending = aidRequestRepository.findByRequestStatus(RequestStatus.PENDING);
        return buildAidRequestReport("PENDING", pending);
    }

    /**
     * FR-80: Fulfilled aid report.
     */
    public AidRequestReport fulfilledRequestsReport() {
        List<AidRequest> fulfilled = aidRequestRepository.findByRequestStatus(RequestStatus.FULFILLED);
        return buildAidRequestReport("FULFILLED", fulfilled);
    }

    /**
     * FR-82: KPI dashboard for leadership.
     */
    public KpiDashboardReport kpiDashboard() {
        return KpiDashboardReport.builder()
                // Campaigns
                .totalCampaigns((int) campaignRepository.count())
                .activeCampaigns((int) campaignRepository.countByCampaignStatus(CampaignStatus.ACTIVE))
                .completedCampaigns((int) campaignRepository.countByCampaignStatus(CampaignStatus.COMPLETED))
                .closedCampaigns((int) campaignRepository.countByCampaignStatus(CampaignStatus.CLOSED))
                // Donations
                .totalDonations(donationRepository.count())
                .totalMonetaryCollected(
                        donationRepository.totalMonetary(null, null) != null
                                ? donationRepository.totalMonetary(null, null)
                                : BigDecimal.ZERO)
                // Aid Requests
                .pendingRequests(aidRequestRepository.countByRequestStatus(RequestStatus.PENDING))
                .approvedRequests(aidRequestRepository.countByRequestStatus(RequestStatus.APPROVED))
                .deniedRequests(aidRequestRepository.countByRequestStatus(RequestStatus.DENIED))
                .fulfilledRequests(aidRequestRepository.countByRequestStatus(RequestStatus.FULFILLED))
                // Inventory
                .totalInventoryItems(inventoryRepository.count())
                .lowStockItems(inventoryRepository.findLowStockItems().size())
                // Users
                .totalActiveUsers(userRepository.countByAccountStatus(AccountStatus.ACTIVE))
                .build();
    }

    // ---- helpers ----

    /**
     * FR-86: Export donation summary as RFC 4180 CSV bytes.
     * Accepts the same filters as donationSummary().
     */
    public byte[] donationSummaryCsv(LocalDateTime from, LocalDateTime to,
                                      Integer donorId, Integer campaignId) {
        DonationSummaryReport report = donationSummary(from, to, donorId, campaignId);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        StringBuilder csv = new StringBuilder();

        // Metadata header rows
        csv.append("CharityAid Donation Summary Report\n");
        csv.append("Generated,").append(LocalDateTime.now().format(fmt)).append("\n");
        if (from != null) csv.append("From,").append(from.format(fmt)).append("\n");
        if (to   != null) csv.append("To,")  .append(to  .format(fmt)).append("\n");
        csv.append("Grand Total Monetary,$").append(report.getGrandTotalMonetary().toPlainString()).append("\n");
        csv.append("Total Donation Count,").append(report.getTotalDonationCount()).append("\n");
        csv.append("\n");

        // Column header
        csv.append("Campaign ID,Campaign Title,Total Amount ($),Donation Count\n");

        // Data rows — escape any commas/quotes in title
        for (DonationSummaryReport.CampaignDonationSummary row : report.getByCampaign()) {
            csv.append(row.getCampaignId()).append(",")
               .append(csvEscape(row.getCampaignTitle())).append(",")
               .append(row.getTotalAmount().toPlainString()).append(",")
               .append(row.getDonationCount()).append("\n");
        }

        return csv.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    private String csvEscape(String value) {
        if (value == null) return "";
        // Wrap in quotes if value contains comma, quote, or newline
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private AidRequestReport buildAidRequestReport(String type, List<AidRequest> requests) {
        List<AidRequestReportItem> items = requests.stream()
                .map(r -> AidRequestReportItem.builder()
                        .requestId(r.getRequestId())
                        .beneficiaryName(r.getBeneficiary() != null ? r.getBeneficiary().getFullName() : null)
                        .aidType(r.getAidType() != null ? r.getAidType().name() : null)
                        .status(r.getRequestStatus().name())
                        .requestedAmount(r.getRequestedAmount())
                        .requestedItem(r.getRequestedItem())
                        .submissionDate(r.getSubmissionDate())
                        .decisionTimestamp(r.getDecisionTimestamp())
                        .fulfillmentDate(r.getFulfillmentDate())
                        .reviewedBy(r.getReviewedBy() != null ? r.getReviewedBy().getFullName() : null)
                        .fulfilledBy(r.getFulfilledBy() != null ? r.getFulfilledBy().getFullName() : null)
                        .build())
                .collect(Collectors.toList());

        return AidRequestReport.builder()
                .reportType(type)
                .totalCount(items.size())
                .requests(items)
                .build();
    }

        // FR-81: scheduled auto-export of donation report
        @Scheduled(cron = "${app.reports.auto-export-cron:0 0 2 * * *}")
        public void autoExportDonationReport() {
                try {
                        byte[] csv = donationSummaryCsv(null, null, null, null);
                        Path dir = Paths.get("exports", "reports");
                        Files.createDirectories(dir);
                        String fileName = "donation-summary-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + ".csv";
                        Files.write(dir.resolve(fileName), csv);
                        auditService.record("REPORT", null, "AUTO_EXPORT", "SYSTEM", "Scheduled report exported: " + fileName);
                } catch (Exception ex) {
                        auditService.record("REPORT", null, "AUTO_EXPORT_FAILED", "SYSTEM", "Scheduled export failed: " + ex.getMessage());
                }
        }
}
