package com.example.order_service.service;

import com.example.order_service.client.ProductClient;
import com.example.order_service.client.UserClient;
import com.example.order_service.dto.CreateOrderItemRequest;
import com.example.order_service.dto.CreateOrderRequest;
import com.example.order_service.dto.ProductResponse;
import com.example.order_service.dto.UserResponse;
import com.example.order_service.entity.Order;
import com.example.order_service.entity.OrderItem;
import com.example.order_service.kafka.OrderCreatedEvent;
import com.example.order_service.kafka.OrderEventProducer;
import com.example.order_service.kafka.OrderItemEvent;
import com.example.order_service.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final UserClient userClient;
    private final OrderEventProducer orderEventProducer;

    public OrderService(OrderRepository orderRepository,
                        ProductClient productClient,
                        UserClient userClient,
                        OrderEventProducer orderEventProducer) {
        this.orderRepository = orderRepository;
        this.productClient = productClient;
        this.userClient = userClient;
        this.orderEventProducer = orderEventProducer;
    }

    @Transactional
    public Order placeOrder(
            Long userId,
            String authorizationHeader,
            CreateOrderRequest request) {

        Order order = new Order();
        order.setUserId(userId);
        order.setStatus("PENDING");

        BigDecimal total = BigDecimal.ZERO;

        for (CreateOrderItemRequest requestedItem : request.getItems()) {

            if (requestedItem.getQuantity() <= 0) {
                throw new IllegalArgumentException(
                        "Quantity must be greater than zero"
                );
            }

            ProductResponse product =
                    productClient.getProduct(
                            requestedItem.getProductId(),
                            authorizationHeader
                    );

            if (product.getQuantity() < requestedItem.getQuantity()) {
                throw new IllegalStateException(
                        "Insufficient inventory for product: "
                                + product.getName()
                );
            }

            BigDecimal subtotal = product.getPrice()
                    .multiply(
                            BigDecimal.valueOf(requestedItem.getQuantity())
                    );

            OrderItem orderItem = new OrderItem(
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    requestedItem.getQuantity(),
                    subtotal
            );

            order.addItem(orderItem);

            total = total.add(subtotal);
        }

        order.setTotalAmount(total);
        Order savedOrder = orderRepository.save(order);
       // return orderRepository.save(order);

        // Build OrderCreatedEvent START

        List<OrderItemEvent> itemEvents =
                savedOrder.getItems()
                        .stream()
                        .map(item -> new OrderItemEvent(
                                item.getProductId(),
                                item.getProductName(),
                                item.getUnitPrice(),
                                item.getQuantity(),
                                item.getSubtotal()
                        ))
                        .toList();

        OrderCreatedEvent event =
                new OrderCreatedEvent(
                        UUID.randomUUID().toString(),
                        "ORDER_CREATED",
                        1,
                        Instant.now().toString(),
                        savedOrder.getId(),
                        savedOrder.getUserId(),
                        savedOrder.getTotalAmount(),
                        itemEvents
                );

        // Build OrderCreatedEvent END
        orderEventProducer.sendOrderCreatedEvent(event);

        return savedOrder;
    }

    @Transactional
    public Order placeOrder(
            String email,
            String authorizationHeader,
            CreateOrderRequest request) {

        UserResponse user =
                userClient.getUserByEmail(email, authorizationHeader);

        return placeOrder(
                user.getId(),
                authorizationHeader,
                request
        );
    }

}