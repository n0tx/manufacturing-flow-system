package com.example.manufacturing_flow.controller;

import com.example.manufacturing_flow.response.ApiResponse;
import com.example.manufacturing_flow.dto.StockAdjustmentRequest;
import com.example.manufacturing_flow.entity.Inventory;
import com.example.manufacturing_flow.entity.InventoryLog;
import com.example.manufacturing_flow.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventories")
@RequiredArgsConstructor
@Tag(name = "Inventory Management", description = "Endpoints for managing raw material stock and logs")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    @Operation(summary = "Get all inventory items", description = "Retrieves a list of all raw materials and their current stock levels")
    public ResponseEntity<ApiResponse<List<Inventory>>> getAllInventory() {
        return ResponseEntity.ok(ApiResponse.success("Inventory data retrieved successfully", inventoryService.getAllInventory()));
    }

    @GetMapping("/{id}/logs")
    @Operation(summary = "Get inventory movement logs", description = "Retrieves historical stock movements (IN/OUT) for a specific material")
    public ResponseEntity<ApiResponse<List<InventoryLog>>> getInventoryLogs(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Inventory logs retrieved successfully", inventoryService.getInventoryLogs(id)));
    }

    @PostMapping("/{id}/add")
    @Operation(summary = "Add stock to inventory", description = "Manually adds quantity to an existing inventory item and logs the movement")
    public ResponseEntity<ApiResponse<Inventory>> addStock(@PathVariable Long id, @RequestBody StockAdjustmentRequest request) {
        Inventory addedInventory = inventoryService.addStock(id, request.getQuantity(), request.getReference(), request.getNotes());
        return ResponseEntity.ok(ApiResponse.success("Stock added successfully", addedInventory));
    }

    @PostMapping("/{id}/reduce")
    @Operation(summary = "Reduce stock from inventory", description = "Manually reduces quantity from an existing inventory item and logs the movement")
    public ResponseEntity<ApiResponse<Inventory>> reduceStock(@PathVariable Long id, @RequestBody StockAdjustmentRequest request) {
        Inventory reducedInventory = inventoryService.reduceStock(id, request.getQuantity(), request.getReference(), request.getNotes());
        return ResponseEntity.ok(ApiResponse.success("Stock reduced successfully", reducedInventory));
    }
}
