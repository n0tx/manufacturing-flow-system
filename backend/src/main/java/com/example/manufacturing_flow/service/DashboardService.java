package com.example.manufacturing_flow.service;

import com.example.manufacturing_flow.dto.DashboardSummary;
import com.example.manufacturing_flow.dto.RevenueChartResponse;
import com.example.manufacturing_flow.dto.TopProductResponse;
import com.example.manufacturing_flow.entity.OrderStatus;
import com.example.manufacturing_flow.entity.ProductionStatus;
import com.example.manufacturing_flow.repository.OrderRepository;
import com.example.manufacturing_flow.repository.PaymentRepository;
import com.example.manufacturing_flow.repository.ProductionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final ProductionRepository productionRepository;

    public DashboardSummary getSummary() {
        BigDecimal totalRevenue = paymentRepository.getTotalRevenue();
        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;

        List<OrderStatus> activeStatuses = Arrays.asList(
                OrderStatus.CREATED,
                OrderStatus.MATERIAL_PREPARED,
                OrderStatus.IN_PRODUCTION,
                OrderStatus.COMPLETED_PRODUCTION,
                OrderStatus.DELIVERED
        );

        long totalProduction = productionRepository.count();
        long failedProduction = productionRepository.countByStatus(ProductionStatus.FAILED);
        long failureRate = totalProduction > 0 ? (failedProduction * 100 / totalProduction) : 0;

        return DashboardSummary.builder()
                .totalOrders(orderRepository.count())
                .created(orderRepository.countByStatus(OrderStatus.CREATED))
                .materialPrepared(orderRepository.countByStatus(OrderStatus.MATERIAL_PREPARED))
                .inProduction(orderRepository.countByStatus(OrderStatus.IN_PRODUCTION))
                .completedProduction(orderRepository.countByStatus(OrderStatus.COMPLETED_PRODUCTION))
                .delivered(orderRepository.countByStatus(OrderStatus.DELIVERED))
                .paid(orderRepository.countByStatus(OrderStatus.PAID))
                .totalRevenue(totalRevenue.longValue())
                .activeOrdersCount(orderRepository.countByStatusIn(activeStatuses))
                .productionFailureRate(failureRate)
                .pendingPaymentsCount(orderRepository.countByStatus(OrderStatus.DELIVERED))
                .build();
    }

    public List<RevenueChartResponse> getRevenueChartData() {
        return paymentRepository.getMonthlyRevenueChartData().stream()
                .map(obj -> RevenueChartResponse.builder()
                        .month((String) obj[0])
                        .revenue((BigDecimal) obj[1])
                        .build())
                .collect(Collectors.toList());
    }

    public List<TopProductResponse> getTopProducts() {
        return orderRepository.getTopProductsData().stream()
                .limit(5)
                .map(obj -> TopProductResponse.builder()
                        .productName((String) obj[0])
                        .totalSold((Long) obj[1])
                        .build())
                .collect(Collectors.toList());
    }
}
