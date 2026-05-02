package org.charityaid.charity_aid.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import org.charityaid.charity_aid.dto.ApiResponse;
import org.charityaid.charity_aid.dto.InventoryMovementRequest;
import org.charityaid.charity_aid.dto.InventoryMovementResponse;
import org.charityaid.charity_aid.dto.InventoryRequest;
import org.charityaid.charity_aid.dto.InventoryResponse;
import org.charityaid.charity_aid.entity.ItemCategory;
import org.charityaid.charity_aid.service.InventoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('STAFF','ADMINISTRATOR')")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<InventoryResponse>>> getAllItems(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getAllItems(pageable)));
    }

    // FR-26: Low stock alert endpoint
    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getLowStockItems() {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getLowStockItems()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InventoryResponse>> getItem(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getItemById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<InventoryResponse>> addItem(@Valid @RequestBody InventoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Item added", inventoryService.addItem(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<InventoryResponse>> updateItem(
            @PathVariable Integer id, @Valid @RequestBody InventoryRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Item updated", inventoryService.updateItem(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<ApiResponse<Void>> deleteItem(@PathVariable Integer id) {
        inventoryService.deleteItem(id);
        return ResponseEntity.ok(ApiResponse.ok("Item deleted", null));
    }

    // FR-63: Inventory category management
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<String>>> getCategories() {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getCategories()));
    }

    // FR-66: Check-in / check-out movement log
    @PostMapping("/{id}/movements")
    public ResponseEntity<ApiResponse<InventoryMovementResponse>> recordMovement(
            @PathVariable Integer id,
            @Valid @RequestBody InventoryMovementRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Movement recorded",
                        inventoryService.recordMovement(id, request, userDetails.getUsername())));
    }

    @GetMapping("/{id}/movements")
    public ResponseEntity<ApiResponse<List<InventoryMovementResponse>>> getMovements(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.getMovements(id)));
    }

    // FR-68: Inventory report endpoint
    @GetMapping("/report")
    public ResponseEntity<ApiResponse<java.util.Map<String, Object>>> inventoryReport(
            @RequestParam(required = false) ItemCategory category) {
        return ResponseEntity.ok(ApiResponse.ok(inventoryService.inventoryReport(category)));
    }
}
