package com.example.order_service.kafka;

import java.math.BigDecimal;

public class OrderItemEvent {

    private Long productId;
    private String productName;
    private BigDecimal unitPrice;
    private int quantity;
    private BigDecimal subtotal;

    public OrderItemEvent() {
    }

    public OrderItemEvent(
            Long productId,
            String productName,
            BigDecimal unitPrice,
            int quantity,
            BigDecimal subtotal) {

        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }
}