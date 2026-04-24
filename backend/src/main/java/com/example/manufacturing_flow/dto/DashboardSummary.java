package com.example.manufacturing_flow.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardSummary {
    private long totalOrders;
    private long created;
    private long materialPrepared;
    private long inProduction;
    private long completedProduction;
    private long delivered;
    private long paid;
}
