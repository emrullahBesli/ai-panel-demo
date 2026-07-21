package dev.emrullah.ai_panel.service;

import dev.emrullah.ai_panel.entity.Order;
import dev.emrullah.ai_panel.entity.OrderUsage;
import dev.emrullah.ai_panel.entity.User;
import dev.emrullah.ai_panel.model.EntityCrudResponse;
import jakarta.persistence.EntityManager;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EntityCrudService {

    private final EntityManager entityManager;

    public EntityCrudService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public EntityCrudResponse createUser(String username, String email) {
        try {
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            entityManager.persist(user);
            return createEntityCrudResponse(user.getId(), null);
        } catch (Exception e) {
            return createEntityCrudResponse(null, e.getMessage());
        }
    }

    public EntityCrudResponse createOrder(String description, java.math.BigDecimal paidAmount, Long userId) {
        try {
            Object queryResult = entityManager.createNamedQuery("SELECT u FROM User u WHERE u.id = " + userId);
            User user = null;
            if (queryResult instanceof List<?> list && !list.isEmpty()) {
                user = (User) list.getFirst();
            } else if (queryResult instanceof User) {
                user = (User) queryResult;
            }

            Order order = new Order();
            order.setDescription(description);
            order.setPaidAmount(paidAmount);
            order.setUser(user);
            entityManager.persist(order);
            return createEntityCrudResponse(order.getId(), null);
        } catch (Exception e) {
            return createEntityCrudResponse(null, e.getMessage());
        }
    }

    public EntityCrudResponse createOrderUsage(Long orderId, String usageDetails) {
        try {
            Object queryResult = entityManager.createNamedQuery("SELECT o FROM Order o WHERE o.id = " + orderId);
            Order order = null;
            if (queryResult instanceof List<?> list && !list.isEmpty()) {
                order = (Order) list.getFirst();
            } else if (queryResult instanceof Order) {
                order = (Order) queryResult;
            }

            OrderUsage orderUsage = new OrderUsage();
            orderUsage.setOrder(order);
            orderUsage.setUsageDetails(usageDetails);
            entityManager.persist(orderUsage);
            return createEntityCrudResponse(orderUsage.getId(), null);
        } catch (Exception e) {
            return createEntityCrudResponse(null, e.getMessage());
        }
    }

    public List<User> listUsers() {
        return entityManager.createQuery("SELECT u FROM User u", User.class).getResultList();
    }

    public List<Order> listOrders() {
        return entityManager.createQuery("SELECT o FROM Order o", Order.class).getResultList();
    }

    public List<OrderUsage> listOrderUsages() {
        return entityManager.createQuery("SELECT ou FROM OrderUsage ou", OrderUsage.class).getResultList();
    }

    private EntityCrudResponse createEntityCrudResponse(@Nullable Long entityId, @Nullable String errorMessage) {
        return new EntityCrudResponse(entityId, errorMessage);
    }
}