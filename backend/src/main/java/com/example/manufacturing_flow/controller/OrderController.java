package com.example.manufacturing_flow.controller;

import com.example.manufacturing_flow.dto.OrderRequest;
import com.example.manufacturing_flow.entity.Order;
import com.example.manufacturing_flow.response.ApiResponse;
import com.example.manufacturing_flow.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Flow 1: Sales / Order", description = "Order Management API")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    @Operation(summary = "Get all orders")
    public ResponseEntity<ApiResponse<List<Order>>> getAllOrders(
        @RequestParam(required = false) String status, 
        @RequestParam(required = false) String customerName) {
        return ResponseEntity.ok(ApiResponse.success("Success", orderService.getAllOrders(status, customerName)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID")
    public ResponseEntity<ApiResponse<Order>> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Success", orderService.getOrderById(id)));
    }

    @PostMapping
    @Operation(summary = "Create a new order")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Order>> createOrder(@RequestBody OrderRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Order created successfully", orderService.createOrder(request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel new order")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok(ApiResponse.success("Order cancelled successfully", null));
    }
}
