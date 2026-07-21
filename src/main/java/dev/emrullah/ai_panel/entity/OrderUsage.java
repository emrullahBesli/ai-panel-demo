package dev.emrullah.ai_panel.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import dev.emrullah.ai_panel.entity.base.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_usages")
public class OrderUsage extends BaseEntity {

    @Column(name = "usage_details")
    private String usageDetails;

    @Column(name = "cost_price", precision = 10, scale = 2)
    private BigDecimal costPrice;

    @OneToOne
    @JsonManagedReference
    @JoinColumn(name = "order_id", unique = true)
    private Order order;

    public OrderUsage() {
    }

    // Getters and Setters


    public String getUsageDetails() {
        return usageDetails;
    }

    public void setUsageDetails(String usageDetails) {
        this.usageDetails = usageDetails;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "OrderUsage{" +
                "usageDetails='" + usageDetails + '\'' +
                ", costPrice=" + costPrice +
                ", order=" + order +
                '}';
    }
}