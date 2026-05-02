package org.charityaid.charity_aid.service;

import java.util.List;
import java.util.stream.Collectors;

import org.charityaid.charity_aid.dto.InventoryMovementRequest;
import org.charityaid.charity_aid.dto.InventoryMovementResponse;
import org.charityaid.charity_aid.dto.InventoryRequest;
import org.charityaid.charity_aid.dto.InventoryResponse;
import org.charityaid.charity_aid.entity.Inventory;
import org.charityaid.charity_aid.entity.InventoryMovement;
import org.charityaid.charity_aid.entity.InventoryMovementType;
import org.charityaid.charity_aid.entity.ItemCategory;
import org.charityaid.charity_aid.entity.RequestStatus;
import org.charityaid.charity_aid.repository.AidRequestRepository;
import org.charityaid.charity_aid.repository.InventoryMovementRepository;
import org.charityaid.charity_aid.repository.InventoryRepository;
import org.charityaid.charity_aid.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMovementRepository inventoryMovementRepository;
    private final AidRequestRepository aidRequestRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;

    public Page<InventoryResponse> getAllItems(Pageable pageable) {
        return inventoryRepository.findAll(pageable).map(InventoryResponse::from);
    }

    public InventoryResponse getItemById(Integer id) {
        return InventoryResponse.from(findOrThrow(id));
    }

    // FR-25 / FR-26: Low stock items only
    public List<InventoryResponse> getLowStockItems() {
        return inventoryRepository.findLowStockItems().stream()
                .map(InventoryResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public InventoryResponse addItem(InventoryRequest request) {
        Inventory item = Inventory.builder()
                .itemName(request.getItemName())
                .itemCategory(request.getItemCategory())
                .quantityOnHand(request.getQuantityOnHand())
                .lowThreshold(request.getLowThreshold())
                .unitOfMeasure(request.getUnitOfMeasure())
                .expiryDate(request.getExpiryDate())
            .supplierName(request.getSupplierName())
            .supplierContact(request.getSupplierContact())
            .warehouseLocation(request.getWarehouseLocation())
            .unitCost(request.getUnitCost())
                .build();

        InventoryResponse response = InventoryResponse.from(inventoryRepository.save(item));
        auditService.record("INVENTORY", response.getItemId(), "CREATE", null,
                "Item added: " + request.getItemName());
        return response;
    }

    @Transactional
    public InventoryResponse updateItem(Integer id, InventoryRequest request) {
        Inventory item = findOrThrow(id);

        item.setItemName(request.getItemName());
        item.setItemCategory(request.getItemCategory());
        item.setQuantityOnHand(request.getQuantityOnHand());
        item.setLowThreshold(request.getLowThreshold());
        item.setUnitOfMeasure(request.getUnitOfMeasure());
        item.setExpiryDate(request.getExpiryDate());
        item.setSupplierName(request.getSupplierName());
        item.setSupplierContact(request.getSupplierContact());
        item.setWarehouseLocation(request.getWarehouseLocation());
        item.setUnitCost(request.getUnitCost());

        InventoryResponse response = InventoryResponse.from(inventoryRepository.save(item));
        auditService.record("INVENTORY", id, "UPDATE", null, "Item updated: " + request.getItemName());
        return response;
    }

    // FR-74: delete only if quantity == 0 and no PENDING aid requests reference this item
    @Transactional
    public void deleteItem(Integer id) {
        Inventory item = findOrThrow(id);

        if (item.getQuantityOnHand() > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cannot delete item with quantity on hand greater than zero");
        }

        if (aidRequestRepository.existsByRequestedItemIgnoreCaseAndRequestStatus(
                item.getItemName(), RequestStatus.PENDING)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cannot delete item that is referenced by a pending aid request");
        }

        String itemName = item.getItemName();
        inventoryRepository.delete(item);
        auditService.record("INVENTORY", id, "DELETE", null, "Item deleted: " + itemName);
    }

    private Inventory findOrThrow(Integer id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inventory item not found"));
    }

    // FR-63
    public List<String> getCategories() {
        return java.util.Arrays.stream(ItemCategory.values()).map(Enum::name).toList();
    }

    // FR-66
    @Transactional
    public InventoryMovementResponse recordMovement(Integer itemId, InventoryMovementRequest request, String actorEmail) {
        Inventory item = findOrThrow(itemId);
        InventoryMovementType movementType;
        try {
            movementType = InventoryMovementType.valueOf(request.getMovementType().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "movementType must be CHECK_IN or CHECK_OUT");
        }

        int quantity = request.getQuantity();
        if (movementType == InventoryMovementType.CHECK_OUT && item.getQuantityOnHand() < quantity) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Insufficient stock for check-out");
        }

        item.setQuantityOnHand(movementType == InventoryMovementType.CHECK_IN
                ? item.getQuantityOnHand() + quantity
                : item.getQuantityOnHand() - quantity);
        inventoryRepository.save(item);

        InventoryMovement movement = inventoryMovementRepository.save(InventoryMovement.builder()
                .inventory(item)
                .movementType(movementType)
                .quantity(quantity)
                .reference(request.getReference())
                .actor(userRepository.findByEmailAddress(actorEmail).orElse(null))
                .build());

        auditService.record("INVENTORY", itemId, "MOVEMENT", actorEmail,
                movementType + " quantity " + quantity);
        return InventoryMovementResponse.from(movement);
    }

    public List<InventoryMovementResponse> getMovements(Integer itemId) {
        findOrThrow(itemId);
        return inventoryMovementRepository.findByInventory_ItemIdOrderByCreatedAtDesc(itemId)
                .stream()
                .map(InventoryMovementResponse::from)
                .toList();
    }

    // FR-68
    public java.util.Map<String, Object> inventoryReport(ItemCategory category) {
        List<Inventory> items = category != null
                ? inventoryRepository.findByItemCategory(category)
                : inventoryRepository.findAll();
        int totalQty = items.stream().mapToInt(Inventory::getQuantityOnHand).sum();
        java.math.BigDecimal totalValue = items.stream()
                .filter(i -> i.getUnitCost() != null)
                .map(i -> i.getUnitCost().multiply(java.math.BigDecimal.valueOf(i.getQuantityOnHand())))
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        return java.util.Map.of(
                "category", category != null ? category.name() : "ALL",
                "itemCount", items.size(),
                "totalQuantity", totalQty,
                "totalEstimatedValue", totalValue,
                "items", items.stream().map(InventoryResponse::from).toList());
    }

    // FR-64: Automated low-stock monitoring
    @Scheduled(fixedDelayString = "${app.inventory.low-stock-check-ms:3600000}")
    @Transactional
    public void automatedLowStockCheck() {
        List<Inventory> lowStockItems = inventoryRepository.findLowStockItems();
        for (Inventory item : lowStockItems) {
            auditService.record("INVENTORY", item.getItemId(), "LOW_STOCK_ALERT", "SYSTEM",
                    "Low stock detected for item " + item.getItemName());
        }
    }
}
