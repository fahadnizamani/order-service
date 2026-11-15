package com.example.order_service.dto;

import java.math.BigDecimal;

public class OrderSummaryResponse {

    private Long id;
    private Long userId;
    private BigDecimal totalAmount;
    private String status;

    public OrderSummaryResponse() {
    }

    public OrderSummaryResponse(Long id, Long userId, BigDecimal totalAmount, String status) {
        this.id = id;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

