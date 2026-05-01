package com.example.manufacturing_flow.controller;

import com.example.manufacturing_flow.response.ApiResponse;
import com.example.manufacturing_flow.dto.StockAdjustmentRequest;
import com.example.manufacturing_flow.entity.Inventory;
import com.example.manufacturing_flow.entity.InventoryLog;
import com.example.manufacturing_flow.service.InventoryService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Inventory>>> getAllInventory() {
        return ResponseEntity.ok(ApiResponse.success("Berhasil mengambil data stok", inventoryService.getAllInventory()));
    }

    @GetMapping("/{id}/logs")
    public ResponseEntity<ApiResponse<List<InventoryLog>>> getInventoryLogs(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Berhasil mengambil riwayat stok", inventoryService.getInventoryLogs(id)));
    }

    @PostMapping("/{id}/add")
    public ResponseEntity<ApiResponse<Inventory>> addStock(@PathVariable Long id, @RequestBody StockAdjustmentRequest request) {
        Inventory addedInventory = inventoryService.addStock(id, request.getQuantity(), request.getReference(), request.getNotes());
        return ResponseEntity.ok(ApiResponse.success("Berhasil menambah stok", addedInventory));
    }

    @PostMapping("/{id}/reduce")
    public ResponseEntity<ApiResponse<Inventory>> reduceStock(@PathVariable Long id, @RequestBody StockAdjustmentRequest request) {
        Inventory reducedInventory = inventoryService.reduceStock(id, request.getQuantity(), request.getReference(), request.getNotes());
        return ResponseEntity.ok(ApiResponse.success("Berhasil mengurangi stok", reducedInventory));
    }
}
