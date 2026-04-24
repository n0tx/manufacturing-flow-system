package com.example.manufacturing_flow.dto;

import lombok.Data;

@Data
public class ReceivingRequest {
    private Long orderId;
    private String rawMaterialNotes;
}
