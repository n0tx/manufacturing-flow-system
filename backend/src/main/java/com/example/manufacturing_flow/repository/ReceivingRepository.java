package com.example.manufacturing_flow.repository;

import com.example.manufacturing_flow.entity.Receiving;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceivingRepository extends JpaRepository<Receiving, Long> {
}
