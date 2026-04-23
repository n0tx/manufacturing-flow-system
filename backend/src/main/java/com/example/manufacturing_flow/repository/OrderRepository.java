package com.example.manufacturing_flow.repository;

import com.example.manufacturing_flow.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
