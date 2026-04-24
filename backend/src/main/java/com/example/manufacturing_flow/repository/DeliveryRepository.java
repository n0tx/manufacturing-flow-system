package com.example.manufacturing_flow.repository;

import com.example.manufacturing_flow.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
}
