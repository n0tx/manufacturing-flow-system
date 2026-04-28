package com.example.manufacturing_flow.service;

import com.example.manufacturing_flow.dto.PaymentRequest;
import com.example.manufacturing_flow.entity.Order;
import com.example.manufacturing_flow.entity.OrderStatus;
import com.example.manufacturing_flow.entity.Payment;
import com.example.manufacturing_flow.repository.OrderRepository;
import com.example.manufacturing_flow.repository.PaymentRepository;
import com.example.manufacturing_flow.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final AuditLogService auditLogService;
    private final SecurityUtils securityUtils;

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Transactional
    public Payment processPayment(PaymentRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new RuntimeException("Error: Order status must be DELIVERED to process payment.");
        }

        // Validasi tambahan: Pastikan jumlah yang dibayar cukup
        if (request.getAmountPaid().compareTo(order.getTotalPrice()) < 0) {
            throw new RuntimeException("Error: Amount paid is less than the total price.");
        } else if (request.getAmountPaid().compareTo(order.getTotalPrice()) > 0) {
            throw new RuntimeException("Error: Amount paid is more than the total price, Please recheck your input!");
        }

        Payment payment = Payment.builder()
                .order(order)
                .paymentMethod(request.getPaymentMethod())
                .amountPaid(request.getAmountPaid())
                .referenceNumber(request.getReferenceNumber())
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        // Update Order Status
        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        // Log Audit
        auditLogService.log(order.getId(), "STATUS_CHANGE", 
            "Status berubah: DELIVERED -> PAID (Pembayaran Diterima)", 
            securityUtils.getCurrentUsername());

        return savedPayment;
    }
}
