package com.example.manufacturing_flow.dto;

import com.example.manufacturing_flow.entity.ProductionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ProductionRequest {
    @Schema(example = "1")
    private Long orderId;
    @Schema(example = "KNITTING")
    private ProductionType productionType;
    @Schema(example = "MESIN-RJT-01")
    private String machineId;
    @Schema(example = "Merajut benang katun combed 30s")
    private String notes;
}
