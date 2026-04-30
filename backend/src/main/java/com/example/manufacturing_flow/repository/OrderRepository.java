package com.example.manufacturing_flow.repository;

import com.example.manufacturing_flow.entity.Order;
import com.example.manufacturing_flow.entity.OrderStatus;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<Order, Long> {
    long countByStatus(OrderStatus status);
    long countByStatusIn(List<OrderStatus> statuses);
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByCustomerNameContainingIgnoreCase(String customerName);
    List<Order> findByStatusAndCustomerNameContainingIgnoreCase(OrderStatus status, String customerName);

    @Query("SELECT o.product.name as productName, SUM(o.quantity) as totalSold " +
           "FROM Order o " +
           "GROUP BY o.product.name " +
           "ORDER BY SUM(o.quantity) DESC")
    List<Object[]> getTopProductsData();
}
