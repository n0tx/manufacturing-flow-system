package com.example.manufacturing_flow.controller;

import com.example.manufacturing_flow.dto.PaymentRequest;
import com.example.manufacturing_flow.entity.Payment;
import com.example.manufacturing_flow.response.ApiResponse;
import com.example.manufacturing_flow.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Flow 5: Finance / Payment", description = "Payment Processing API")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    @Operation(summary = "Get all payment records")
    public ResponseEntity<ApiResponse<List<Payment>>> getAllPayments() {
        return ResponseEntity.ok(ApiResponse.success("Success", paymentService.getAllPayments()));
    }

    @PostMapping
    @Operation(summary = "Process payment for a delivered order")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE')")
    public ResponseEntity<ApiResponse<Payment>> processPayment(@RequestBody PaymentRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Payment processed successfully", paymentService.processPayment(request)));
    }
}
