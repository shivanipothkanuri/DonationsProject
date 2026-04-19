package org.charityaid.charity_aid.service;

import java.util.List;
import java.util.stream.Collectors;

import org.charityaid.charity_aid.dto.InventoryRequest;
import org.charityaid.charity_aid.dto.InventoryResponse;
import org.charityaid.charity_aid.entity.Inventory;
import org.charityaid.charity_aid.entity.RequestStatus;
import org.charityaid.charity_aid.repository.AidRequestRepository;
import org.charityaid.charity_aid.repository.InventoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final AidRequestRepository aidRequestRepository;
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
}
