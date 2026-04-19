package org.charityaid.charity_aid.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;

/**
 * FR-82: KPI dashboard for Leadership — totals across all key dimensions.
 */
@Getter
@Builder
public class KpiDashboardReport {

    // Campaigns
    private int totalCampaigns;
    private int activeCampaigns;
    private int completedCampaigns;
    private int closedCampaigns;

    // Donations
    private long totalDonations;
    private BigDecimal totalMonetaryCollected;

    // Aid Requests
    private long pendingRequests;
    private long approvedRequests;
    private long deniedRequests;
    private long fulfilledRequests;

    // Inventory
    private long totalInventoryItems;
    private long lowStockItems;

    // Users
    private long totalActiveUsers;
}
