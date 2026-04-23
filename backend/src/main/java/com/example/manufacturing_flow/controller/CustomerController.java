package com.example.manufacturing_flow.controller;

import com.example.manufacturing_flow.entity.Customer;
import com.example.manufacturing_flow.response.ApiResponse;
import com.example.manufacturing_flow.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "Master Data: Customer", description = "Customer Management API")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    @Operation(summary = "Get all customers")
    public ResponseEntity<ApiResponse<List<Customer>>> getAllCustomers() {
        return ResponseEntity.ok(ApiResponse.success("Success", customerService.getAllCustomers()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID")
    public ResponseEntity<ApiResponse<Customer>> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Success", customerService.getCustomerById(id)));
    }

    @PostMapping
    @Operation(summary = "Create a new customer")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Customer>> createCustomer(@RequestBody Customer customer) {
        return ResponseEntity.ok(ApiResponse.success("Customer created", customerService.createCustomer(customer)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing customer")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Customer>> updateCustomer(@PathVariable Long id, @RequestBody Customer customer) {
        return ResponseEntity.ok(ApiResponse.success("Customer updated", customerService.updateCustomer(id, customer)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a customer")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok(ApiResponse.success("Customer deleted", null));
    }
}
