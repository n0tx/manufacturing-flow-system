package com.example.manufacturing_flow.controller;

import com.example.manufacturing_flow.dto.ProductionRequest;
import com.example.manufacturing_flow.entity.Order;
import com.example.manufacturing_flow.entity.Production;
import com.example.manufacturing_flow.response.ApiResponse;
import com.example.manufacturing_flow.service.ProductionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/productions")
@RequiredArgsConstructor
@Tag(name = "Flow 3: Factory / Production", description = "Production Logging API")
public class ProductionController {

    private final ProductionService productionService;

    @GetMapping
    @Operation(summary = "Get all production logs")
    public ResponseEntity<ApiResponse<List<Production>>> getAllProductions() {
        return ResponseEntity.ok(ApiResponse.success("Success", productionService.getAllProductions()));
    }

    @PostMapping
    @Operation(summary = "Log a production activity (Knitting, Dyeing, Printing)")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUKSI')")
    public ResponseEntity<ApiResponse<Production>> logActivity(@RequestBody ProductionRequest request, Principal principal) {
        String username = principal != null ? principal.getName() : "system";
        return ResponseEntity.ok(ApiResponse.success("Production logged successfully", productionService.logProductionActivity(request, username)));
    }

    @PostMapping("/{orderId}/finish")
    @Operation(summary = "Mark production as completed for an order")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUKSI')")
    public ResponseEntity<ApiResponse<Order>> finishProduction(@PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.success("Production completed successfully", productionService.finishProduction(orderId)));
    }
}
