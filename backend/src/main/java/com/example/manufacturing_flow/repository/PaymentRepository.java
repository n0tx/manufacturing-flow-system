package com.example.manufacturing_flow.repository;

import com.example.manufacturing_flow.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.Query;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("SELECT SUM(p.amountPaid) FROM Payment p")
    BigDecimal getTotalRevenue();

    @Query(value = "SELECT to_char(payment_date, 'Mon') as month, SUM(amount_paid) as revenue " +
                   "FROM payments " +
                   "WHERE payment_date >= now() - interval '6 months' " +
                   "GROUP BY to_char(payment_date, 'Mon'), date_trunc('month', payment_date) " +
                   "ORDER BY date_trunc('month', payment_date) ASC", nativeQuery = true)
    List<Object[]> getMonthlyRevenueChartData();
}
