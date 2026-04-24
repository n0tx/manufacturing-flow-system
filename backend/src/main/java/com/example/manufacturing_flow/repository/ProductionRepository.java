package com.example.manufacturing_flow.repository;

import com.example.manufacturing_flow.entity.Production;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductionRepository extends JpaRepository<Production, Long> {
}
