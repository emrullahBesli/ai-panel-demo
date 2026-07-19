package dev.emrullah.ai_panel.bootstrap;

import dev.emrullah.ai_panel.entity.Order;
import dev.emrullah.ai_panel.entity.OrderUsage;
import dev.emrullah.ai_panel.entity.User;
import jakarta.persistence.EntityManager;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    private final EntityManager entityManager;

    public DataInitializer(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        Long userCount = entityManager.createQuery("SELECT COUNT(u) FROM User u", Long.class).getSingleResult();

        if (userCount == 0) {
            User user = new User();
            user.setUsername("demo_user");
            user.setEmail("demo@example.com");
            entityManager.persist(user);

            Order orderWithUsage = new Order();
            orderWithUsage.setDescription("AI Image Generation (Success)");
            orderWithUsage.setPaidAmount(new BigDecimal("150.00"));
            orderWithUsage.setUser(user);
            entityManager.persist(orderWithUsage);

            OrderUsage usage = new OrderUsage();
            usage.setUsageDetails("Used 10 GPU Credits");
            usage.setCostPrice(new BigDecimal("25.50"));
            usage.setOrder(orderWithUsage);
            entityManager.persist(usage);

            Order orderWithoutUsage = new Order();
            orderWithoutUsage.setDescription("AI Text Generation (Pending/Failed)");
            orderWithoutUsage.setPaidAmount(new BigDecimal("50.00"));
            orderWithoutUsage.setUser(user);
            entityManager.persist(orderWithoutUsage);
            

            User user2 = new User();
            user2.setUsername("ahmet");
            user2.setEmail("ahmet@example.com");
            entityManager.persist(user2);

            Order order2 = new Order();
            order2.setDescription("AI Video Generation (Success)");
            order2.setPaidAmount(new BigDecimal("450.00"));
            order2.setUser(user2);
            entityManager.persist(order2);

            OrderUsage usage2 = new OrderUsage();
            usage2.setUsageDetails("Used 120 GPU Credits, 4K Render");
            usage2.setCostPrice(new BigDecimal("85.75"));
            usage2.setOrder(order2);
            entityManager.persist(usage2);

            User user3 = new User();
            user3.setUsername("ayse");
            user3.setEmail("ayse@example.com");
            entityManager.persist(user3);

            Order order3 = new Order();
            order3.setDescription("AI Voice Cloning (Success)");
            order3.setPaidAmount(new BigDecimal("300.00"));
            order3.setUser(user3);
            entityManager.persist(order3);

            OrderUsage usage3 = new OrderUsage();
            usage3.setUsageDetails("Used 50 Audio Credits");
            usage3.setCostPrice(new BigDecimal("45.50"));
            usage3.setOrder(order3);
            entityManager.persist(usage3);

            Order order4 = new Order();
            order4.setDescription("AI Voice Cloning (Failed/Refunded)");
            order4.setPaidAmount(new BigDecimal("300.00"));
            order4.setUser(user3);
            entityManager.persist(order4);
            
            User user4 = new User();
            user4.setUsername("mehmet");
            user4.setEmail("mehmet@example.com");
            entityManager.persist(user4);

            System.out.println("H2 Veritabanına başlangıç örnek verileri (Seed Data) eklendi.");
        }
    }
}