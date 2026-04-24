package com.example.manufacturing_flow.service;

import com.example.manufacturing_flow.dto.DeliveryRequest;
import com.example.manufacturing_flow.entity.Delivery;
import com.example.manufacturing_flow.entity.Order;
import com.example.manufacturing_flow.entity.OrderStatus;
import com.example.manufacturing_flow.repository.DeliveryRepository;
import com.example.manufacturing_flow.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;

    public List<Delivery> getAllDeliveries() {
        return deliveryRepository.findAll();
    }

    @Transactional
    public Delivery processDelivery(DeliveryRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() != OrderStatus.COMPLETED_PRODUCTION) {
            throw new RuntimeException("Error: Order status must be COMPLETED_PRODUCTION to process delivery.");
        }

        Delivery delivery = Delivery.builder()
                .order(order)
                .trackingNumber(request.getTrackingNumber())
                .driverName(request.getDriverName())
                .vehiclePlate(request.getVehiclePlate())
                .build();

        Delivery savedDelivery = deliveryRepository.save(delivery);

        // Update Order Status
        order.setStatus(OrderStatus.DELIVERED);
        orderRepository.save(order);

        return savedDelivery;
    }
}
