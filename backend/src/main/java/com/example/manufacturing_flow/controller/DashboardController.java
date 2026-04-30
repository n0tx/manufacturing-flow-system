package com.example.manufacturing_flow.controller;

import com.example.manufacturing_flow.dto.DashboardSummary;
import com.example.manufacturing_flow.dto.RevenueChartResponse;
import com.example.manufacturing_flow.dto.TopProductResponse;
import com.example.manufacturing_flow.response.ApiResponse;
import com.example.manufacturing_flow.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.example.manufacturing_flow.security.SecurityRoles.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Analytics and summary for management")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    @HasAnyDashboardRole
    @Operation(summary = "Get dashboard summary stats")
    public ResponseEntity<ApiResponse<DashboardSummary>> getSummary() {
        return ResponseEntity.ok(ApiResponse.success("Success", dashboardService.getSummary()));
    }

    @GetMapping("/revenue-chart")
    @IsAdmin
    @Operation(summary = "Get monthly revenue chart data")
    public ResponseEntity<ApiResponse<List<RevenueChartResponse>>> getRevenueChart() {
        return ResponseEntity.ok(ApiResponse.success("Success", dashboardService.getRevenueChartData()));
    }

    @GetMapping("/top-products")
    @IsAdmin
    @Operation(summary = "Get top 5 products by sales")
    public ResponseEntity<ApiResponse<List<TopProductResponse>>> getTopProducts() {
        return ResponseEntity.ok(ApiResponse.success("Success", dashboardService.getTopProducts()));
    }
}
