package com.example.order_service.controller;

import com.example.order_service.entity.Order;
import com.example.order_service.service.OrderService;
import org.springframework.web.bind.annotation.*;
import com.example.order_service.dto.CreateOrderRequest;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping
    public Order placeOrder(
            @RequestHeader("X-User-Email") String email,
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody CreateOrderRequest request) {

        System.out.println(
                ">>> ORDER-SERVICE received order request for: " + email
        );

        return service.placeOrder(
                email,
                authorizationHeader,
                request
        );
    }
}