package com.example.manufacturing_flow.repository;

import com.example.manufacturing_flow.entity.InventoryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InventoryLogRepository extends JpaRepository<InventoryLog, Long> {
    List<InventoryLog> findByInventoryIdOrderByTimestampDesc(Long inventoryId);
}
