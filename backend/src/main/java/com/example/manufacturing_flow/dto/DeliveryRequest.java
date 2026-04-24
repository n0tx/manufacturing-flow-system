package com.example.manufacturing_flow.dto;

import lombok.Data;

@Data
public class DeliveryRequest {
    private Long orderId;
    private String trackingNumber;
    private String driverName;
    private String vehiclePlate;
}
