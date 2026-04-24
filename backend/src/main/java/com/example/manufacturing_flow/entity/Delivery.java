package com.example.manufacturing_flow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "deliveries")
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private String trackingNumber;

    @Column(nullable = false)
    private String driverName;

    @Column(nullable = false)
    private String vehiclePlate;

    @Column(nullable = false, updatable = false)
    private LocalDateTime deliveryDate;

    @PrePersist
    protected void onCreate() {
        deliveryDate = LocalDateTime.now();
    }
}
