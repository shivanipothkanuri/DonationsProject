package org.charityaid.charity_aid.entity;

import java.math.BigDecimal;
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
@Table(name = "tbl_recurring_donations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecurringDonation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RECURRING_DONATION_ID")
    private Integer recurringDonationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DONOR_ID", nullable = false)
    private User donor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CAMPAIGN_ID", nullable = false)
    private Campaign campaign;

    @Column(name = "DONATION_AMOUNT", nullable = false, precision = 10, scale = 2)
    private BigDecimal donationAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "PAYMENT_METHOD", length = 30)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "FREQUENCY", nullable = false, length = 20)
    private RecurringDonationFrequency frequency;

    @Column(name = "NEXT_RUN_AT", nullable = false)
    private LocalDateTime nextRunAt;

    @Column(name = "LAST_PROCESSED_AT")
    private LocalDateTime lastProcessedAt;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "CANCELLED_AT")
    private LocalDateTime cancelledAt;

    @Column(name = "ACTIVE", nullable = false)
    @Builder.Default
    private boolean active = true;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (active && nextRunAt == null) {
            nextRunAt = createdAt.plusMonths(1);
        }
    }
}