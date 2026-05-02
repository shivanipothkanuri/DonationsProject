package org.charityaid.charity_aid.repository;

import java.util.List;

import org.charityaid.charity_aid.entity.InventoryMovement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Integer> {

    List<InventoryMovement> findByInventory_ItemIdOrderByCreatedAtDesc(Integer itemId);
}
