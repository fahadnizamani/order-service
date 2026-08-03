package com.example.order_service.kafka;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderCreatedEvent {

    private String eventId;
    private String eventType;
    private int eventVersion;
    private String occurredAt;

    private Long orderId;
    private Long userId;
    private BigDecimal totalAmount;
    private List<OrderItemEvent> items;

    public OrderCreatedEvent() {
    }

    public OrderCreatedEvent(
            String eventId,
            String eventType,
            int eventVersion,
            String occurredAt,
            Long orderId,
            Long userId,
            BigDecimal totalAmount,
            List<OrderItemEvent> items) {

        this.eventId = eventId;
        this.eventType = eventType;
        this.eventVersion = eventVersion;
        this.occurredAt = occurredAt;
        this.orderId = orderId;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.items = items;
    }

    public String getEventId() {
        return eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public int getEventVersion() {
        return eventVersion;
    }

    public String getOccurredAt() {
        return occurredAt;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public List<OrderItemEvent> getItems() {
        return items;
    }
}