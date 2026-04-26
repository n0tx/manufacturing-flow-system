package com.example.manufacturing_flow.service;

import com.example.manufacturing_flow.dto.OrderRequest;
import com.example.manufacturing_flow.entity.Customer;
import com.example.manufacturing_flow.entity.Order;
import com.example.manufacturing_flow.entity.OrderStatus;
import com.example.manufacturing_flow.entity.Product;
import com.example.manufacturing_flow.repository.OrderRepository;
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

    public List<Order> getAllOrders(String status) {
        if (status == null || status.isBlank()) {
            return orderRepository.findAll();
        }
        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            return orderRepository.findByStatus(orderStatus);
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

        return orderRepository.save(order);
    }
}
