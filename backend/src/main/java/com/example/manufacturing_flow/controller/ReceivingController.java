package com.example.manufacturing_flow.controller;

import com.example.manufacturing_flow.dto.ReceivingRequest;
import com.example.manufacturing_flow.entity.Receiving;
import com.example.manufacturing_flow.response.ApiResponse;
import com.example.manufacturing_flow.service.ReceivingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/receivings")
@RequiredArgsConstructor
@Tag(name = "Flow 2: Warehouse / Receiving", description = "Material Receiving API")
public class ReceivingController {

    private final ReceivingService receivingService;

    @GetMapping
    @Operation(summary = "Get all receivings")
    public ResponseEntity<ApiResponse<List<Receiving>>> getAllReceivings() {
        return ResponseEntity.ok(ApiResponse.success("Success", receivingService.getAllReceivings()));
    }

    @PostMapping
    @Operation(summary = "Process material receiving for an order")
    @PreAuthorize("hasAnyRole('ADMIN', 'GUDANG')")
    public ResponseEntity<ApiResponse<Receiving>> createReceiving(@RequestBody ReceivingRequest request, Principal principal) {
        // Extract the username of the currently logged-in user
        String username = principal != null ? principal.getName() : "system";
        return ResponseEntity.ok(ApiResponse.success("Material received successfully", receivingService.createReceiving(request, username)));
    }
}
