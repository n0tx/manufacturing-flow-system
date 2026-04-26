package com.example.manufacturing_flow.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class OrderRequest {
    @Schema(example = "1")
    private Long customerId;
    @Schema(example = "1")
    private Long productId;
    @Schema(example = "100")
    private Integer quantity;
}
