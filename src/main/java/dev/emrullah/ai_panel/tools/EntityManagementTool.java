package dev.emrullah.ai_panel.tools;

import dev.emrullah.ai_panel.model.EntityCrudResponse;
import dev.emrullah.ai_panel.service.EntityCrudService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class EntityManagementTool {

    private final EntityCrudService entityCrudService;

    public EntityManagementTool(EntityCrudService entityCrudService) {
        this.entityCrudService = entityCrudService;
    }

    @Tool(name = "create_user", description = "Registers a new user in the system. Use this tool when a user wants to create a new account by providing their username and email.")
    public String createUser(
            @ToolParam(description = "The unique username for the new user.") String username,
            @ToolParam(description = "The valid email address of the new user.") String email
    ) {
        EntityCrudResponse entityCrudResponse = entityCrudService.createUser(username, email);
        if (entityCrudResponse.errorMessage() == null) {
            return "User created successfully with ID: " + entityCrudResponse.entityId();

        }
        return " user cannot creating. error message: " + entityCrudResponse.errorMessage();
    }

    @Tool(name = "create_order", description = "Creates a new order record linked to a user. Use this tool when a user makes a purchase, requiring a description, total cost, and the associated user's ID.")
    public String createOrder(
            @ToolParam(description = "A concise description of the order or purchased items.") String description,
            @ToolParam(description = "The total monetary amount paid for the order.") BigDecimal paidAmount,
            @ToolParam(description = "The database ID of the user who made the purchase.") Long userId
    ) {
        EntityCrudResponse entityCrudResponse = entityCrudService.createOrder(description, paidAmount, userId);
        if (entityCrudResponse.errorMessage() == null) {
            return "order created successfully with ID: " + entityCrudResponse.entityId();

        }
        return " order cannot creating. error message: " + entityCrudResponse.errorMessage();
    }

    @Tool(name = "create_order_usage", description = "Logs a new usage entry for a specific order. Use this tool when tracking how an existing order is being utilized, requiring the order's ID and usage details.")
    public String createOrderUsage(
            @ToolParam(description = "The database ID of the existing order.") Long orderId,
            @ToolParam(description = "Detailed information describing how the order is being used.") String usageDetails
    ) {

        EntityCrudResponse entityCrudResponse = entityCrudService.createOrderUsage(orderId, usageDetails);
        if (entityCrudResponse.errorMessage() == null) {
            return "order usage created successfully with ID: " + entityCrudResponse.entityId();

        }
        return " order usage cannot creating. error message: " + entityCrudResponse.errorMessage();
    }
}
