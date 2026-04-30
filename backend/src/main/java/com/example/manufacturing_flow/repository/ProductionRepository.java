package com.example.manufacturing_flow.repository;

import com.example.manufacturing_flow.entity.Production;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.manufacturing_flow.entity.ProductionStatus;

public interface ProductionRepository extends JpaRepository<Production, Long> {
    long countByStatus(ProductionStatus status);
}
