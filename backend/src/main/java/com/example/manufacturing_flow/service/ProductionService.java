package com.example.manufacturing_flow.service;

import com.example.manufacturing_flow.dto.ProductionRequest;
import com.example.manufacturing_flow.entity.Order;
import com.example.manufacturing_flow.entity.OrderStatus;
import com.example.manufacturing_flow.entity.Production;
import com.example.manufacturing_flow.repository.OrderRepository;
import com.example.manufacturing_flow.repository.ProductionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductionService {

    private final ProductionRepository productionRepository;
    private final OrderRepository orderRepository;

    public List<Production> getAllProductions() {
        return productionRepository.findAll();
    }

    @Transactional
    public Production logProductionActivity(ProductionRequest request, String username) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Validasi: Produksi hanya bisa dilakukan jika bahan sudah siap atau sedang dalam masa produksi
        if (order.getStatus() != OrderStatus.MATERIAL_PREPARED && order.getStatus() != OrderStatus.IN_PRODUCTION) {
            throw new RuntimeException("Error: Order status must be MATERIAL_PREPARED or IN_PRODUCTION to log activity.");
        }

        Production production = Production.builder()
                .order(order)
                .productionType(request.getProductionType())
                .machineId(request.getMachineId())
                .notes(request.getNotes())
                .operatorName(username)
                .build();

        Production savedProduction = productionRepository.save(production);

        // Ubah status ke IN_PRODUCTION jika ini adalah log pertama
        if (order.getStatus() == OrderStatus.MATERIAL_PREPARED) {
            order.setStatus(OrderStatus.IN_PRODUCTION);
            orderRepository.save(order);
        }

        return savedProduction;
    }

    @Transactional
    public Order finishProduction(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() != OrderStatus.IN_PRODUCTION) {
            throw new RuntimeException("Error: Only orders with status IN_PRODUCTION can be finished.");
        }

        order.setStatus(OrderStatus.COMPLETED_PRODUCTION);
        return orderRepository.save(order);
    }
}
