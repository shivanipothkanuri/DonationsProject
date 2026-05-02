package org.charityaid.charity_aid.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.charityaid.charity_aid.entity.Inventory;
import org.charityaid.charity_aid.entity.ItemCategory;
import org.charityaid.charity_aid.entity.UnitOfMeasure;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InventoryResponse {

    private Integer itemId;
    private String itemName;
    private ItemCategory itemCategory;
    private int quantityOnHand;
    private int lowThreshold;
    private boolean lowStock;
    private UnitOfMeasure unitOfMeasure;
    private LocalDate expiryDate;
    private LocalDateTime lastUpdated;
    private String supplierName;
    private String supplierContact;
    private String warehouseLocation;
    private BigDecimal unitCost;
    private BigDecimal totalValue;

    public static InventoryResponse from(Inventory i) {
        return InventoryResponse.builder()
                .itemId(i.getItemId())
                .itemName(i.getItemName())
                .itemCategory(i.getItemCategory())
                .quantityOnHand(i.getQuantityOnHand())
                .lowThreshold(i.getLowThreshold())
                .lowStock(i.getQuantityOnHand() <= i.getLowThreshold())
                .unitOfMeasure(i.getUnitOfMeasure())
                .expiryDate(i.getExpiryDate())
                .lastUpdated(i.getLastUpdated())
                .supplierName(i.getSupplierName())
                .supplierContact(i.getSupplierContact())
                .warehouseLocation(i.getWarehouseLocation())
                .unitCost(i.getUnitCost())
                .totalValue(i.getUnitCost() != null
                    ? i.getUnitCost().multiply(BigDecimal.valueOf(i.getQuantityOnHand()))
                    : null)
                .build();
    }
}
