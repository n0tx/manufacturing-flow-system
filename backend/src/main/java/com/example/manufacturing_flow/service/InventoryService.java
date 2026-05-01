package com.example.manufacturing_flow.service;

import com.example.manufacturing_flow.entity.Inventory;
import com.example.manufacturing_flow.entity.InventoryLog;
import com.example.manufacturing_flow.entity.StockMovementType;
import com.example.manufacturing_flow.exception.InsufficientStockException;
import com.example.manufacturing_flow.repository.InventoryLogRepository;
import com.example.manufacturing_flow.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryLogRepository inventoryLogRepository;

    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    public List<InventoryLog> getInventoryLogs(Long inventoryId) {
        return inventoryLogRepository.findByInventoryIdOrderByTimestampDesc(inventoryId);
    }

    @Transactional
    public Inventory addStock(Long inventoryId, BigDecimal quantity, String reference, String notes) {
        Inventory inventory = inventoryRepository.findByIdWithLock(inventoryId)
                .orElseThrow(() -> new RuntimeException("Material not found"));

        inventory.setStockQuantity(inventory.getStockQuantity().add(quantity));
        Inventory updated = inventoryRepository.save(inventory);

        saveLog(updated, quantity, StockMovementType.IN, reference, notes);
        return updated;
    }

    @Transactional
    public Inventory reduceStock(Long inventoryId, BigDecimal quantity, String reference, String notes) {
        Inventory inventory = inventoryRepository.findByIdWithLock(inventoryId)
                .orElseThrow(() -> new RuntimeException("Material not found"));

        if (inventory.getStockQuantity().compareTo(quantity) < 0) {
            throw new InsufficientStockException("Stok tidak mencukupi untuk bahan: " + inventory.getMaterialName());
        }

        inventory.setStockQuantity(inventory.getStockQuantity().subtract(quantity));
        Inventory updated = inventoryRepository.save(inventory);

        saveLog(updated, quantity, StockMovementType.OUT, reference, notes);
        return updated;
    }

    private void saveLog(Inventory inventory, BigDecimal quantity, StockMovementType type, String reference, String notes) {
        InventoryLog log = InventoryLog.builder()
                .inventory(inventory)
                .quantity(quantity)
                .type(type)
                .reference(reference)
                .notes(notes)
                .build();
        inventoryLogRepository.save(log);
    }
}
