package org.charityaid.charity_aid.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tbl_inventory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ITEM_ID")
    private Integer itemId;

    @Column(name = "ITEM_NAME", nullable = false, length = 150)
    private String itemName;

    @Enumerated(EnumType.STRING)
    @Column(name = "ITEM_CATEGORY", nullable = false, length = 50)
    private ItemCategory itemCategory;

    @Column(name = "QUANTITY_ON_HAND", nullable = false)
    private int quantityOnHand;

    @Column(name = "LOW_THRESHOLD", nullable = false)
    private int lowThreshold;

    @Enumerated(EnumType.STRING)
    @Column(name = "UNIT_OF_MEASURE", nullable = false, length = 30)
    private UnitOfMeasure unitOfMeasure;

    // FR-65: Supplier metadata
    @Column(name = "SUPPLIER_NAME", length = 120)
    private String supplierName;

    @Column(name = "SUPPLIER_CONTACT", length = 120)
    private String supplierContact;

    // FR-75: Multi-location inventory
    @Column(name = "WAREHOUSE_LOCATION", length = 100)
    private String warehouseLocation;

    // FR-76: Inventory valuation
    @Column(name = "UNIT_COST", precision = 10, scale = 2)
    private BigDecimal unitCost;

    @Column(name = "EXPIRY_DATE")
    private LocalDate expiryDate;

    @Column(name = "LAST_UPDATED", nullable = false)
    private LocalDateTime lastUpdated;

    @PrePersist
    @PreUpdate
    protected void onSave() {
        lastUpdated = LocalDateTime.now();
    }
}
