package org.charityaid.charity_aid.dto;

import java.time.LocalDateTime;

import org.charityaid.charity_aid.entity.InventoryMovement;
import org.charityaid.charity_aid.entity.InventoryMovementType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InventoryMovementResponse {

    private Integer movementId;
    private Integer itemId;
    private String itemName;
    private InventoryMovementType movementType;
    private int quantity;
    private String reference;
    private Integer donationId;
    private String actorName;
    private LocalDateTime createdAt;

    public static InventoryMovementResponse from(InventoryMovement m) {
        return InventoryMovementResponse.builder()
                .movementId(m.getMovementId())
                .itemId(m.getInventory().getItemId())
                .itemName(m.getInventory().getItemName())
                .movementType(m.getMovementType())
                .quantity(m.getQuantity())
                .reference(m.getReference())
                .donationId(m.getDonationId())
                .actorName(m.getActor() != null ? m.getActor().getFullName() : null)
                .createdAt(m.getCreatedAt())
                .build();
    }
}
