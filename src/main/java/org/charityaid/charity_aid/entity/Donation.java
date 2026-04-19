package org.charityaid.charity_aid.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

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
@Table(name = "tbl_donations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Donation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DONATION_ID")
    private Integer donationId;

    @Column(name = "TRANSACTION_ID", nullable = false, unique = true, length = 50)
    private String transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DONOR_ID", nullable = false)
    private User donor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CAMPAIGN_ID", nullable = false)
    private Campaign campaign;

    @Enumerated(EnumType.STRING)
    @Column(name = "DONATION_TYPE", nullable = false, length = 20)
    private DonationType donationType;

    @Column(name = "DONATION_AMOUNT", precision = 10, scale = 2)
    private BigDecimal donationAmount;

    @Column(name = "ITEM_DESCRIPTION", length = 255)
    private String itemDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "PAYMENT_METHOD", length = 30)
    private PaymentMethod paymentMethod;

    @Column(name = "DONATION_DATE", nullable = false, updatable = false)
    private LocalDateTime donationDate;

    @Column(name = "RECEIPT_GENERATED", nullable = false)
    @Builder.Default
    private boolean receiptGenerated = false;

    @PrePersist
    protected void onCreate() {
        donationDate = LocalDateTime.now();
        if (transactionId == null) {
            // e.g. TXN-8F3A2C1B
            transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }
}
