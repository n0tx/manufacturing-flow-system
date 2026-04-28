package com.example.manufacturing_flow.repository;

import com.example.manufacturing_flow.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByOrderIdOrderByTimestampDesc(Long orderId);
}
