package com.example.manufacturing_flow.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class DeliveryRequest {
    @Schema(example = "1")
    private Long orderId;
    @Schema(example = "SJ-2026-001")
    private String trackingNumber;
    @Schema(example = "Budi Santoso")
    private String driverName;
    @Schema(example = "B 1234 CD")
    private String vehiclePlate;
}
