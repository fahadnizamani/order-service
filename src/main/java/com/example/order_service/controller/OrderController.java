package com.example.order_service.controller;

import com.example.order_service.client.UserClient;
import com.example.order_service.dto.CreateOrderRequest;
import com.example.order_service.dto.OrderSummaryResponse;
import com.example.order_service.dto.UserResponse;
import com.example.order_service.entity.Order;
import com.example.order_service.repository.OrderRepository;
import com.example.order_service.security.OrderJwtValidator;
import com.example.order_service.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService service;
    private final OrderRepository orderRepository;
    private final UserClient userClient;
    private final OrderJwtValidator orderJwtValidator;

    public OrderController(OrderService service,
                           OrderRepository orderRepository,
                           UserClient userClient,
                           OrderJwtValidator orderJwtValidator) {
        this.service = service;
        this.orderRepository = orderRepository;
        this.userClient = userClient;
        this.orderJwtValidator = orderJwtValidator;
    }

    @PostMapping
    public Order placeOrder(
            @RequestHeader(value = "X-User-Email", required = false) String email,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody CreateOrderRequest request) {

        String authenticatedEmail = orderJwtValidator.validateAndExtractEmail(authorizationHeader, email);

        System.out.println(
                ">>> ORDER-SERVICE received order request for: " + authenticatedEmail
        );

        return service.placeOrder(
                authenticatedEmail,
                authorizationHeader,
                request
        );
    }

    @GetMapping("/{orderId}")
    public OrderSummaryResponse getOrder(
            @PathVariable Long orderId,
            @RequestHeader(value = "X-User-Email", required = false) String email,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {

        String authenticatedEmail = orderJwtValidator.validateAndExtractEmail(authorizationHeader, email);

        UserResponse authenticatedUser =
                userClient.getUserByEmail(authenticatedEmail, authorizationHeader);

        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
        }

        Order order = orderOptional.get();
        if (!order.getUserId().equals(authenticatedUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Order access denied");
        }

        return new OrderSummaryResponse(
                order.getId(),
                order.getUserId(),
                order.getTotalAmount(),
                order.getStatus()
        );
    }
}