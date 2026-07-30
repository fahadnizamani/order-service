package com.example.order_service.service;

import com.example.order_service.client.ProductClient;
import com.example.order_service.client.UserClient;
import com.example.order_service.dto.CreateOrderItemRequest;
import com.example.order_service.dto.CreateOrderRequest;
import com.example.order_service.dto.ProductResponse;
import com.example.order_service.dto.UserResponse;
import com.example.order_service.entity.Order;
import com.example.order_service.entity.OrderItem;
import com.example.order_service.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final UserClient userClient;

    public OrderService(OrderRepository orderRepository,
                        ProductClient productClient,
                        UserClient userClient) {
        this.orderRepository = orderRepository;
        this.productClient = productClient;
        this.userClient = userClient;
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

        return orderRepository.save(order);
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