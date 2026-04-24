package com.example.manufacturing_flow.repository;

import com.example.manufacturing_flow.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
