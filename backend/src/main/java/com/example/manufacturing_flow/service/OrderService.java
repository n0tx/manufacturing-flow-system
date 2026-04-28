package com.example.manufacturing_flow.service;

import com.example.manufacturing_flow.dto.OrderRequest;
import com.example.manufacturing_flow.entity.Customer;
import com.example.manufacturing_flow.entity.Order;
import com.example.manufacturing_flow.entity.OrderStatus;
import com.example.manufacturing_flow.entity.Product;
import com.example.manufacturing_flow.repository.OrderRepository;
import com.example.manufacturing_flow.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerService customerService;
    private final ProductService productService;
    private final AuditLogService auditLogService;
    private final SecurityUtils securityUtils;

    public List<Order> getAllOrders(String status, String customerName) {
        boolean hasStatus = status != null && !status.isBlank();
        boolean hasName = customerName != null && !customerName.isBlank();

        // Kasus 1: Tidak ada filter sama sekali
        if (!hasStatus && !hasName) {
            return orderRepository.findAll();
        }

        // Kasus 2: Cuma cari Nama Customer
        if (!hasStatus) {
            return orderRepository.findByCustomerNameContainingIgnoreCase(customerName);
        }

        // Kasus 3: Cari berdasarkan Status (dan mungkin Nama)
        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            
            if (hasName) {
                // Cari Status DAN Nama
                return orderRepository.findByStatusAndCustomerNameContainingIgnoreCase(orderStatus, customerName);
            } else {
                // Cuma cari Status
                return orderRepository.findByStatus(orderStatus);
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid order status: " + status);
        }
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public Order createOrder(OrderRequest request) {
        Customer customer = customerService.getCustomerById(request.getCustomerId());
        Product product = productService.getProductById(request.getProductId());

        BigDecimal totalPrice = product.getPrice().multiply(new BigDecimal(request.getQuantity()));

        Order order = Order.builder()
                .customer(customer)
                .product(product)
                .quantity(request.getQuantity())
                .totalPrice(totalPrice)
                .build();

        Order savedOrder = orderRepository.save(order);
        
        // Log Audit
        auditLogService.log(savedOrder.getId(), "ORDER_CREATED", 
            "Pesanan baru dibuat untuk customer: " + customer.getName(), 
            securityUtils.getCurrentUsername());

        return savedOrder;
    }

    public void deleteOrder(Long id) {
        Order order = getOrderById(id);
        if (order.getStatus() != OrderStatus.CREATED) {
            throw new RuntimeException("Tidak dapat membatalkan karena order sudah diproses!");
        }
        orderRepository.delete(order);
        
        // Log Audit
        auditLogService.log(id, "ORDER_CANCELLED", 
            "Pesanan dibatalkan oleh Admin", 
            securityUtils.getCurrentUsername());
    }
}
