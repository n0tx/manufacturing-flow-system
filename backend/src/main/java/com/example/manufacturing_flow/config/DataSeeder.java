package com.example.manufacturing_flow.config;

import com.example.manufacturing_flow.entity.*;
import com.example.manufacturing_flow.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "app.db.seed", havingValue = "true")
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (customerRepository.count() > 0) {
            log.info("Database already contains data. Skipping seeder.");
            return;
        }

        log.info("Starting database seeder...");

        // 1. Seed Users (Roles)
        userRepository.save(new User(null, "admin", passwordEncoder.encode("admin123"), Role.ADMIN));
        userRepository.save(new User(null, "gudang", passwordEncoder.encode("gudang123"), Role.GUDANG));
        userRepository.save(new User(null, "produksi", passwordEncoder.encode("produksi123"), Role.PRODUKSI));
        userRepository.save(new User(null, "finance", passwordEncoder.encode("finance123"), Role.FINANCE));
        log.info("Seeded 4 users (admin, gudang, produksi, finance)");

        // 2. Seed Customers
        Customer c1 = customerRepository.save(new Customer(null, "PT. Maju Jaya", "contact@majujaya.com", "081234567890", "Jl. Sudirman No. 10, Jakarta"));
        Customer c2 = customerRepository.save(new Customer(null, "CV. Sandang Perkasa", "info@sandang.com", "089876543210", "Jl. Merdeka No. 45, Bandung"));
        log.info("Seeded 2 customers");

        // 3. Seed Products
        Product p1 = productRepository.save(new Product(null, "Kain Katun Combed 30s", "SKU-KATUN-01", new BigDecimal("75000"), "Kain rajut kualitas tinggi"));
        Product p2 = productRepository.save(new Product(null, "Kain Polyester", "SKU-POLY-01", new BigDecimal("45000"), "Kain sintetis tahan lama"));
        log.info("Seeded 2 products");

        // 4. Seed Orders with different statuses
        
        // Order 1: CREATED
        Order o1 = new Order();
        o1.setCustomer(c1);
        o1.setProduct(p1);
        o1.setQuantity(100);
        o1.setTotalPrice(p1.getPrice().multiply(new BigDecimal("100")));
        o1.setStatus(OrderStatus.CREATED);
        o1.setOrderDate(LocalDateTime.now().minusDays(1));
        orderRepository.save(o1);

        // Order 2: MATERIAL_PREPARED
        Order o2 = new Order();
        o2.setCustomer(c2);
        o2.setProduct(p2);
        o2.setQuantity(200);
        o2.setTotalPrice(p2.getPrice().multiply(new BigDecimal("200")));
        o2.setStatus(OrderStatus.MATERIAL_PREPARED);
        o2.setOrderDate(LocalDateTime.now().minusDays(2));
        orderRepository.save(o2);

        // Order 3: IN_PRODUCTION
        Order o3 = new Order();
        o3.setCustomer(c1);
        o3.setProduct(p2);
        o3.setQuantity(150);
        o3.setTotalPrice(p2.getPrice().multiply(new BigDecimal("150")));
        o3.setStatus(OrderStatus.IN_PRODUCTION);
        o3.setOrderDate(LocalDateTime.now().minusDays(3));
        orderRepository.save(o3);

        // Order 4: COMPLETED_PRODUCTION
        Order o4 = new Order();
        o4.setCustomer(c2);
        o4.setProduct(p1);
        o4.setQuantity(50);
        o4.setTotalPrice(p1.getPrice().multiply(new BigDecimal("50")));
        o4.setStatus(OrderStatus.COMPLETED_PRODUCTION);
        o4.setOrderDate(LocalDateTime.now().minusDays(4));
        orderRepository.save(o4);
        
        // Order 5: DELIVERED
        Order o5 = new Order();
        o5.setCustomer(c1);
        o5.setProduct(p1);
        o5.setQuantity(80);
        o5.setTotalPrice(p1.getPrice().multiply(new BigDecimal("80")));
        o5.setStatus(OrderStatus.DELIVERED);
        o5.setOrderDate(LocalDateTime.now().minusDays(5));
        orderRepository.save(o5);

        // Order 6: PAID
        Order o6 = new Order();
        o6.setCustomer(c2);
        o6.setProduct(p2);
        o6.setQuantity(300);
        o6.setTotalPrice(p2.getPrice().multiply(new BigDecimal("300")));
        o6.setStatus(OrderStatus.PAID);
        o6.setOrderDate(LocalDateTime.now().minusDays(10));
        orderRepository.save(o6);

        log.info("Seeded 6 orders with diverse statuses");
        log.info("Database seeder completed successfully!");
    }
}
