package com.example.manufacturing_flow.controller;

import com.example.manufacturing_flow.entity.Product;
import com.example.manufacturing_flow.response.ApiResponse;
import com.example.manufacturing_flow.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Master Data: Product", description = "Product Management API")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Get all products")
    public ResponseEntity<ApiResponse<List<Product>>> getAllProducts() {
        return ResponseEntity.ok(ApiResponse.success("Success", productService.getAllProducts()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    public ResponseEntity<ApiResponse<Product>> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Success", productService.getProductById(id)));
    }

    @PostMapping
    @Operation(summary = "Create a new product")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Product>> createProduct(@RequestBody Product product) {
        return ResponseEntity.ok(ApiResponse.success("Product created", productService.createProduct(product)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing product")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Product>> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        return ResponseEntity.ok(ApiResponse.success("Product updated", productService.updateProduct(id, product)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted", null));
    }
}
