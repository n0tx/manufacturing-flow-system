package com.example.manufacturing_flow.repository;

import com.example.manufacturing_flow.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
