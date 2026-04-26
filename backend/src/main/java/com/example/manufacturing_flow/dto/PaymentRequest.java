package com.example.manufacturing_flow.dto;

import com.example.manufacturing_flow.entity.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequest {
    @Schema(example = "1")
    private Long orderId;
    @Schema(example = "BANK_TRANSFER")
    private PaymentMethod paymentMethod;
    @Schema(example = "5500000.00")
    private BigDecimal amountPaid;
    @Schema(example = "BCA-998877")
    private String referenceNumber;
}
