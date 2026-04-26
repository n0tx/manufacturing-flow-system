package com.example.manufacturing_flow.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ReceivingRequest {
    @Schema(example = "1")
    private Long orderId;
    @Schema(example = "Benang Katun Combed Putih 1000kg")
    private String rawMaterialNotes;
}
