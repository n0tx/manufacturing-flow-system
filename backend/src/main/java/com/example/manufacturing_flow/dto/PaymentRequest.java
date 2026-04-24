package com.example.manufacturing_flow.dto;

import com.example.manufacturing_flow.entity.PaymentMethod;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequest {
    private Long orderId;
    private PaymentMethod paymentMethod;
    private BigDecimal amountPaid;
    private String referenceNumber;
}
