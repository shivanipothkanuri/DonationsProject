package org.charityaid.charity_aid.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tbl_campaigns")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CAMPAIGN_ID")
    private Integer campaignId;

    @Column(name = "CAMPAIGN_TITLE", nullable = false, length = 150)
    private String campaignTitle;

    @Column(name = "DESCRIPTION", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "GOAL_AMOUNT", nullable = false, precision = 10, scale = 2)
    private BigDecimal goalAmount;

    @Column(name = "COLLECTED_AMOUNT", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal collectedAmount = BigDecimal.ZERO;

    @Column(name = "START_DATE", nullable = false)
    private LocalDate startDate;

    @Column(name = "END_DATE", nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "CAMPAIGN_STATUS", nullable = false, length = 20)
    @Builder.Default
    private CampaignStatus campaignStatus = CampaignStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(name = "CATEGORY", length = 50)
    private CampaignCategory category;

    // FR-71: Public share slug
    @Column(name = "SHARE_SLUG", unique = true, length = 120)
    private String shareSlug;

    // FR-70: Milestone tracking flags
    @Column(name = "MILESTONE_25_SENT", nullable = false)
    @Builder.Default
    private boolean milestone25Sent = false;

    @Column(name = "MILESTONE_50_SENT", nullable = false)
    @Builder.Default
    private boolean milestone50Sent = false;

    @Column(name = "MILESTONE_75_SENT", nullable = false)
    @Builder.Default
    private boolean milestone75Sent = false;

    @Column(name = "MILESTONE_100_SENT", nullable = false)
    @Builder.Default
    private boolean milestone100Sent = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CREATED_BY", nullable = false)
    private User createdBy;

    @Column(name = "CREATED_DATE", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        if (collectedAmount == null) {
            collectedAmount = BigDecimal.ZERO;
        }
        if (campaignStatus == null) {
            campaignStatus = CampaignStatus.ACTIVE;
        }
        if (shareSlug == null || shareSlug.isBlank()) {
            shareSlug = (campaignTitle != null ? campaignTitle : "campaign")
                    .toLowerCase()
                    .replaceAll("[^a-z0-9]+", "-")
                    .replaceAll("(^-|-$)", "")
                    + "-" + java.util.UUID.randomUUID().toString().substring(0, 8);
        }
    }
}
