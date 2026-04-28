package com.example.manufacturing_flow.controller;

import com.example.manufacturing_flow.entity.AuditLog;
import com.example.manufacturing_flow.response.ApiResponse;
import com.example.manufacturing_flow.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
@Tag(name = "Utility: Audit Log", description = "API to fetch order history")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping("/{orderId}")
    @Operation(summary = "Get audit logs for a specific order")
    public ResponseEntity<ApiResponse<List<AuditLog>>> getOrderLogs(@PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.success("Success", auditLogService.getLogsByOrderId(orderId)));
    }
}
