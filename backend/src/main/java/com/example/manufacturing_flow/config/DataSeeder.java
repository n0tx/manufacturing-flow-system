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
    private final PaymentRepository paymentRepository;
    private final ProductionRepository productionRepository;
    private final AuditLogRepository auditLogRepository;
    private final ReceivingRepository receivingRepository;
    private final DeliveryRepository deliveryRepository;
    private final InventoryRepository inventoryRepository;
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

        // 1.5 Seed Inventory (Materials)
        if (inventoryRepository.count() == 0) {
            inventoryRepository.save(new Inventory(null, "BNG-001", "Benang Katun 30s", new BigDecimal("1000"), "KG", new BigDecimal("100")));
            inventoryRepository.save(new Inventory(null, "KIN-001", "Kain Putih (Raw)", new BigDecimal("500"), "METER", new BigDecimal("50")));
            inventoryRepository.save(new Inventory(null, "PWR-001", "Pewarna Biru Indigo", new BigDecimal("100"), "KG", new BigDecimal("10")));
            log.info("Seeded 3 initial inventory materials");
        }

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


        // F. Historical Revenue for Chart (Using separate orders to avoid One-to-One constraint violation)
        Order oHistory1 = orderRepository.save(Order.builder()
                .customer(c2).product(p2).quantity(100).status(OrderStatus.PAID)
                .totalPrice(new BigDecimal("15000000"))
                .orderDate(LocalDateTime.now().minusMonths(1)).build());
        
        paymentRepository.save(Payment.builder()
                .order(oHistory1)
                .amountPaid(oHistory1.getTotalPrice())
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .referenceNumber("REF-HIST-001")
                .paymentDate(LocalDateTime.now().minusMonths(1))
                .build());

        Order oHistory2 = orderRepository.save(Order.builder()
                .customer(c1).product(p1).quantity(50).status(OrderStatus.PAID)
                .totalPrice(new BigDecimal("12000000"))
                .orderDate(LocalDateTime.now().minusMonths(2)).build());

        paymentRepository.save(Payment.builder()
                .order(oHistory2)
                .amountPaid(oHistory2.getTotalPrice())
                .paymentMethod(PaymentMethod.CASH)
                .referenceNumber("REF-HIST-002")
                .paymentDate(LocalDateTime.now().minusMonths(2))
                .build());

        // === THE MASTER ORDER (FULL FLOW 9 STEPS) ===
        // We do this last so it gets the highest ID and appears at the top
        Customer masterCust = customerRepository.save(new Customer(null, "PT. Maju Jaya (FULL FLOW)", "boss@majujaya.com", "081122334455", "Jakarta"));
        
        Order masterOrder = Order.builder()
                .customer(masterCust)
                .product(p1)
                .quantity(100)
                .totalPrice(p1.getPrice().multiply(new BigDecimal("100")))
                .status(OrderStatus.PAID)
                .orderDate(LocalDateTime.now())
                .build();
        masterOrder = orderRepository.save(masterOrder);

        // A. Audit Logs for Master Order (9 Steps)
        auditLogRepository.save(new AuditLog(null, masterOrder.getId(), "ORDER_CREATED", "Pesanan baru dibuat untuk customer: PT. Maju Jaya (FULL FLOW)", "admin", LocalDateTime.now().minusDays(5)));
        auditLogRepository.save(new AuditLog(null, masterOrder.getId(), "STATUS_CHANGE", "Status berubah: CREATED -> MATERIAL_PREPARED (Bahan Baku Diterima)", "gudang", LocalDateTime.now().minusDays(4).plusHours(2)));
        auditLogRepository.save(new AuditLog(null, masterOrder.getId(), "STATUS_CHANGE", "Status berubah: MATERIAL_PREPARED -> IN_PRODUCTION (Aktivitas Produksi Dimulai)", "produksi", LocalDateTime.now().minusDays(3).plusHours(1)));
        auditLogRepository.save(new AuditLog(null, masterOrder.getId(), "PRODUCTION_ACTIVITY", "Aktivitas: KNITTING pada mesin MESIN-RJT-01", "produksi", LocalDateTime.now().minusDays(3).plusHours(2)));
        auditLogRepository.save(new AuditLog(null, masterOrder.getId(), "PRODUCTION_ACTIVITY", "Aktivitas: DYEING pada mesin MESIN-DYE-02", "produksi", LocalDateTime.now().minusDays(3).plusHours(4)));
        auditLogRepository.save(new AuditLog(null, masterOrder.getId(), "PRODUCTION_ACTIVITY", "Aktivitas: PRINTING pada mesin MESIN-PRT-01", "produksi", LocalDateTime.now().minusDays(2).plusHours(1)));
        auditLogRepository.save(new AuditLog(null, masterOrder.getId(), "STATUS_CHANGE", "Status berubah: IN_PRODUCTION -> COMPLETED_PRODUCTION (Produksi Selesai)", "produksi", LocalDateTime.now().minusDays(2).plusHours(2)));
        auditLogRepository.save(new AuditLog(null, masterOrder.getId(), "STATUS_CHANGE", "Status berubah: COMPLETED_PRODUCTION -> DELIVERED (Pesanan Dikirim)", "gudang", LocalDateTime.now().minusDays(1).plusHours(5)));
        auditLogRepository.save(new AuditLog(null, masterOrder.getId(), "STATUS_CHANGE", "Status berubah: DELIVERED -> PAID (Pembayaran Diterima)", "finance", LocalDateTime.now().minusDays(1).plusHours(8)));

        // B. Related Data for Master Order
        receivingRepository.save(Receiving.builder().order(masterOrder).rawMaterialNotes("Diterima benang 1500kg").receivedBy("gudang").receivedDate(LocalDateTime.now().minusDays(4)).build());
        productionRepository.save(Production.builder().order(masterOrder).productionType(ProductionType.KNITTING).operatorName("produksi").machineId("MC-01").status(ProductionStatus.SUCCESS).loggedAt(LocalDateTime.now().minusDays(3)).build());
        deliveryRepository.save(Delivery.builder().order(masterOrder).driverName("Budi").vehiclePlate("B 1234 CD").trackingNumber("SJ-777").deliveryDate(LocalDateTime.now().minusDays(1)).build());
        paymentRepository.save(Payment.builder().order(masterOrder).amountPaid(masterOrder.getTotalPrice()).paymentMethod(PaymentMethod.BANK_TRANSFER).referenceNumber("REF-MASTER").paymentDate(LocalDateTime.now().minusDays(1)).build());

        // G. Failed Production Simulation
        productionRepository.save(Production.builder().order(o3).productionType(ProductionType.KNITTING).operatorName("Andi").machineId("ERR-01").status(ProductionStatus.FAILED).notes("Error").loggedAt(LocalDateTime.now()).build());

        log.info("Master Order with 9 logs seeded at the end with ID #" + masterOrder.getId());
        
        // H. SYNC HISTORY & TRANSACTIONAL DATA
        
        // o5: DELIVERED (Log + Data)
        createAuditLog(o5, "ORDER_CREATED", "Pesanan baru dibuat");
        saveLog(o5.getId(), "STATUS_CHANGE", "Status berubah: -> MATERIAL_PREPARED", "gudang", o5.getOrderDate().plusHours(2));
        saveLog(o5.getId(), "STATUS_CHANGE", "Status berubah: -> IN_PRODUCTION", "produksi", o5.getOrderDate().plusDays(1));
        saveLog(o5.getId(), "STATUS_CHANGE", "Status berubah: -> COMPLETED_PRODUCTION", "produksi", o5.getOrderDate().plusDays(2));
        saveLog(o5.getId(), "STATUS_CHANGE", "Status berubah: -> DELIVERED", "gudang", o5.getOrderDate().plusDays(3));
        seedReceiving(o5); 
        seedProduction(o5, ProductionType.KNITTING, ProductionStatus.SUCCESS);
        seedProduction(o5, ProductionType.DYEING, ProductionStatus.SUCCESS);
        seedProduction(o5, ProductionType.PRINTING, ProductionStatus.SUCCESS);
        seedDelivery(o5);

        // o4: COMPLETED_PRODUCTION (Log + Data)
        createAuditLog(o4, "ORDER_CREATED", "Pesanan baru dibuat");
        saveLog(o4.getId(), "STATUS_CHANGE", "Status berubah: -> MATERIAL_PREPARED", "gudang", o4.getOrderDate().plusHours(2));
        saveLog(o4.getId(), "STATUS_CHANGE", "Status berubah: -> IN_PRODUCTION", "produksi", o4.getOrderDate().plusDays(1));
        saveLog(o4.getId(), "STATUS_CHANGE", "Status berubah: -> COMPLETED_PRODUCTION", "produksi", o4.getOrderDate().plusDays(2));
        seedReceiving(o4);
        seedProduction(o4, ProductionType.KNITTING, ProductionStatus.SUCCESS);
        seedProduction(o4, ProductionType.DYEING, ProductionStatus.SUCCESS);
        seedProduction(o4, ProductionType.PRINTING, ProductionStatus.SUCCESS);

        // o3: IN_PRODUCTION (Log + Data)
        createAuditLog(o3, "ORDER_CREATED", "Pesanan baru dibuat");
        saveLog(o3.getId(), "STATUS_CHANGE", "Status berubah: -> MATERIAL_PREPARED", "gudang", o3.getOrderDate().plusHours(2));
        saveLog(o3.getId(), "STATUS_CHANGE", "Status berubah: -> IN_PRODUCTION", "produksi", o3.getOrderDate().plusDays(1));
        seedReceiving(o3);
        seedProduction(o3, ProductionType.KNITTING, ProductionStatus.SUCCESS);

        // o2: MATERIAL_PREPARED (Log + Data)
        createAuditLog(o2, "ORDER_CREATED", "Pesanan baru dibuat");
        saveLog(o2.getId(), "STATUS_CHANGE", "Status berubah: -> MATERIAL_PREPARED", "gudang", o2.getOrderDate().plusHours(2));
        seedReceiving(o2);

        // o6: PAID (Log + Data)
        createAuditLog(o6, "ORDER_CREATED", "Pesanan baru dibuat");
        saveLog(o6.getId(), "STATUS_CHANGE", "Status berubah: -> PAID", "finance", o6.getOrderDate().plusDays(1));
        seedReceiving(o6);
        seedProduction(o6, ProductionType.KNITTING, ProductionStatus.SUCCESS);
        seedProduction(o6, ProductionType.DYEING, ProductionStatus.SUCCESS);
        seedProduction(o6, ProductionType.PRINTING, ProductionStatus.SUCCESS);
        seedDelivery(o6);
        seedPayment(o6, "REF-PAID-06");

        // Histori Payments
        createAuditLog(oHistory1, "ORDER_CREATED", "Pesanan histori dibuat");
        saveLog(oHistory1.getId(), "STATUS_CHANGE", "Status berubah: -> PAID", "system", oHistory1.getOrderDate().plusHours(1));
        createAuditLog(oHistory2, "ORDER_CREATED", "Pesanan histori dibuat");
        saveLog(oHistory2.getId(), "STATUS_CHANGE", "Status berubah: -> PAID", "system", oHistory2.getOrderDate().plusHours(1));

        // Basic o1
        createAuditLog(o1, "ORDER_CREATED", "Pesanan baru dibuat");

        log.info("Database seeder completed with 100% Sync between Logs and Transactional Data!");
    }

    private void seedReceiving(Order order) {
        receivingRepository.save(Receiving.builder()
                .order(order).receivedBy("gudang")
                .rawMaterialNotes("Bahan baku untuk Order #" + order.getId())
                .receivedDate(order.getOrderDate().plusHours(1)).build());
    }

    private void seedProduction(Order order, ProductionType type, ProductionStatus status) {
        productionRepository.save(Production.builder()
                .order(order).productionType(type).operatorName("produksi")
                .machineId("MC-" + type.name().substring(0, 3))
                .status(status).loggedAt(order.getOrderDate().plusDays(1)).build());
    }

    private void seedDelivery(Order order) {
        deliveryRepository.save(Delivery.builder()
                .order(order).driverName("Kurir Internal")
                .vehiclePlate("B 9999 MF").trackingNumber("TRK-" + order.getId())
                .deliveryDate(order.getOrderDate().plusDays(3)).build());
    }

    private void seedPayment(Order order, String ref) {
        paymentRepository.save(Payment.builder()
                .order(order).amountPaid(order.getTotalPrice())
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .referenceNumber(ref).paymentDate(order.getOrderDate().plusDays(4)).build());
    }

    private void saveLog(Long orderId, String action, String details, String user, LocalDateTime ts) {
        auditLogRepository.save(new AuditLog(null, orderId, action, details, user, ts));
    }

    private void createAuditLog(Order order, String action, String details) {
        auditLogRepository.save(AuditLog.builder()
                .orderId(order.getId())
                .action(action)
                .details(details)
                .username("system")
                .timestamp(order.getOrderDate())
                .build());
    }
}
