package org.charityaid.charity_aid.dto;

import org.charityaid.charity_aid.entity.ItemCategory;
import org.charityaid.charity_aid.entity.UnitOfMeasure;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

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
}
