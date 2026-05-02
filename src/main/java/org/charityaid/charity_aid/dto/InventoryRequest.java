package org.charityaid.charity_aid.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.charityaid.charity_aid.entity.ItemCategory;
import org.charityaid.charity_aid.entity.UnitOfMeasure;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryRequest {

    @NotBlank(message = "Item name is required")
    @Size(max = 150)
    private String itemName;

    @NotNull(message = "Item category is required")
    private ItemCategory itemCategory;

    @NotNull(message = "Quantity on hand is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantityOnHand;

    @NotNull(message = "Low threshold is required")
    @Min(value = 0)
    private Integer lowThreshold;

    @NotNull(message = "Unit of measure is required")
    private UnitOfMeasure unitOfMeasure;

    private LocalDate expiryDate;

    @Size(max = 120)
    private String supplierName;

    @Size(max = 120)
    private String supplierContact;

    @Size(max = 100)
    private String warehouseLocation;

    @DecimalMin(value = "0.0", inclusive = true, message = "Unit cost cannot be negative")
    private BigDecimal unitCost;
}
