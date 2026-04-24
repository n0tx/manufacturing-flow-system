package com.example.manufacturing_flow.controller;

import com.example.manufacturing_flow.dto.DashboardSummary;
import com.example.manufacturing_flow.entity.OrderStatus;
import com.example.manufacturing_flow.repository.OrderRepository;
import com.example.manufacturing_flow.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Dashboard Summary API")
public class DashboardController {

    private final OrderRepository orderRepository;

    @GetMapping("/summary")
    @Operation(summary = "Get dashboard summary of all orders")
    public ResponseEntity<ApiResponse<DashboardSummary>> getSummary() {
        DashboardSummary summary = DashboardSummary.builder()
                .totalOrders(orderRepository.count())
                .created(orderRepository.countByStatus(OrderStatus.CREATED))
                .materialPrepared(orderRepository.countByStatus(OrderStatus.MATERIAL_PREPARED))
                .inProduction(orderRepository.countByStatus(OrderStatus.IN_PRODUCTION))
                .completedProduction(orderRepository.countByStatus(OrderStatus.COMPLETED_PRODUCTION))
                .delivered(orderRepository.countByStatus(OrderStatus.DELIVERED))
                .paid(orderRepository.countByStatus(OrderStatus.PAID))
                .build();
        return ResponseEntity.ok(ApiResponse.success("Success", summary));
    }
}
