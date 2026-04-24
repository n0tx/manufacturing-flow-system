package com.example.manufacturing_flow.dto;

import com.example.manufacturing_flow.entity.ProductionType;
import lombok.Data;

@Data
public class ProductionRequest {
    private Long orderId;
    private ProductionType productionType;
    private String machineId;
    private String notes;
}
