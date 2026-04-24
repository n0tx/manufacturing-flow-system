package com.example.manufacturing_flow.service;

import com.example.manufacturing_flow.dto.ReceivingRequest;
import com.example.manufacturing_flow.entity.Order;
import com.example.manufacturing_flow.entity.OrderStatus;
import com.example.manufacturing_flow.entity.Receiving;
import com.example.manufacturing_flow.repository.OrderRepository;
import com.example.manufacturing_flow.repository.ReceivingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReceivingService {

    private final ReceivingRepository receivingRepository;
    private final OrderRepository orderRepository;

    public List<Receiving> getAllReceivings() {
        return receivingRepository.findAll();
    }

    @Transactional
    public Receiving createReceiving(ReceivingRequest request, String username) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // KUNCI ALUR BISNIS (The Flow Lock)
        if (order.getStatus() != OrderStatus.CREATED) {
            throw new RuntimeException("Error: Order status must be CREATED to process receiving. Current status is " + order.getStatus());
        }

        Receiving receiving = Receiving.builder()
                .order(order)
                .rawMaterialNotes(request.getRawMaterialNotes())
                .receivedBy(username)
                .build();

        Receiving savedReceiving = receivingRepository.save(receiving);

        // Update Order Status
        order.setStatus(OrderStatus.MATERIAL_PREPARED);
        orderRepository.save(order);

        return savedReceiving;
    }
}
