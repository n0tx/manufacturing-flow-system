package com.example.manufacturing_flow.controller;

import com.example.manufacturing_flow.dto.DeliveryRequest;
import com.example.manufacturing_flow.entity.Delivery;
import com.example.manufacturing_flow.response.ApiResponse;
import com.example.manufacturing_flow.service.DeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
@Tag(name = "Flow 4: Logistics / Delivery", description = "Delivery Management API")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @GetMapping
    @Operation(summary = "Get all deliveries")
    public ResponseEntity<ApiResponse<List<Delivery>>> getAllDeliveries() {
        return ResponseEntity.ok(ApiResponse.success("Success", deliveryService.getAllDeliveries()));
    }

    @PostMapping
    @Operation(summary = "Process delivery for a completed order")
    @PreAuthorize("hasAnyRole('ADMIN', 'GUDANG')")
    public ResponseEntity<ApiResponse<Delivery>> processDelivery(@RequestBody DeliveryRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Delivery processed successfully", deliveryService.processDelivery(request)));
    }
}
