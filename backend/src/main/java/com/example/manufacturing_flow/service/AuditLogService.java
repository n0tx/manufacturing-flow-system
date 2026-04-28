package com.example.manufacturing_flow.service;

import com.example.manufacturing_flow.entity.AuditLog;
import com.example.manufacturing_flow.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Transactional
    public void log(Long orderId, String action, String details, String username) {
        AuditLog log = AuditLog.builder()
                .orderId(orderId)
                .action(action)
                .details(details)
                .username(username)
                .build();
        auditLogRepository.save(log);
    }

    public List<AuditLog> getLogsByOrderId(Long orderId) {
        return auditLogRepository.findByOrderIdOrderByTimestampDesc(orderId);
    }
}
