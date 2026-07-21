package dev.emrullah.ai_panel.controller;

import dev.emrullah.ai_panel.entity.Order;
import dev.emrullah.ai_panel.entity.OrderUsage;
import dev.emrullah.ai_panel.entity.User;
import dev.emrullah.ai_panel.model.EntityCrudResponse;
import dev.emrullah.ai_panel.service.EntityCrudService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/entities")
public class EntityCrudController {

    private final EntityCrudService entityCrudService;

    public EntityCrudController(EntityCrudService entityCrudService) {
        this.entityCrudService = entityCrudService;
    }

    @PostMapping("/users")
    public ResponseEntity<EntityCrudResponse> createUser(@RequestParam String username, @RequestParam String email) {
        return ResponseEntity.ok(entityCrudService.createUser(username, email));
    }

    @PostMapping("/orders")
    public ResponseEntity<EntityCrudResponse> createOrder(@RequestParam String description, @RequestParam BigDecimal paidAmount, @RequestParam Long userId) {
        return ResponseEntity.ok(entityCrudService.createOrder(description, paidAmount, userId));
    }

    @PostMapping("/order-usages")
    public ResponseEntity<EntityCrudResponse> createOrderUsage(@RequestParam Long orderId, @RequestParam String usageDetails) {
        return ResponseEntity.ok(entityCrudService.createOrderUsage(orderId, usageDetails));
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> listUsers() {
        return ResponseEntity.ok(entityCrudService.listUsers());
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> listOrders() {
        return ResponseEntity.ok(entityCrudService.listOrders());
    }

    @GetMapping("/order-usages")
    public ResponseEntity<List<OrderUsage>> listOrderUsages() {
        return ResponseEntity.ok(entityCrudService.listOrderUsages());
    }
}
