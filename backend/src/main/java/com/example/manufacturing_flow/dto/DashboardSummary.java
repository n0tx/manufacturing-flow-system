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
    private long totalRevenue; // sum payments.amount_paid
    private long activeOrdersCount; // jumlah order != COMPLTED atau CANCELED
    private long productionFailureRate; // persentasi produksi yang gagal vs total produksi
    private long pendingPaymentsCount;// jumlah order yang sudah dikirim tapi belum lunas
}
