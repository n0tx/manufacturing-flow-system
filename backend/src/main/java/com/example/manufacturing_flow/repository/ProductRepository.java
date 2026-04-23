package com.example.manufacturing_flow.repository;

import com.example.manufacturing_flow.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
