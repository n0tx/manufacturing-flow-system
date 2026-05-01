package com.example.manufacturing_flow.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class StockAdjustmentRequest {
    private BigDecimal quantity;
    private String reference;
    private String notes;
}
