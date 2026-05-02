package org.charityaid.charity_aid.repository;

import java.util.List;

import org.charityaid.charity_aid.entity.Inventory;
import org.charityaid.charity_aid.entity.ItemCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InventoryRepository extends JpaRepository<Inventory, Integer> {

    // Items at or below their low stock threshold (FR low-stock alert)
    @Query("SELECT i FROM Inventory i WHERE i.quantityOnHand <= i.lowThreshold")
    List<Inventory> findLowStockItems();

    List<Inventory> findAllByOrderByItemNameAsc();

    List<Inventory> findByItemCategory(ItemCategory itemCategory);

    boolean existsByItemNameIgnoreCase(String itemName);
}
