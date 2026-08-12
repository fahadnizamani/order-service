package com.example.order_service.kafka;

import com.example.order_service.entity.Order;
import com.example.order_service.repository.OrderRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentProcessedConsumer {

    private final OrderRepository orderRepository;

    public PaymentProcessedConsumer(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @KafkaListener(
            topics = "payment-processed",
            groupId = "order-service"
    )
    public void consume(PaymentProcessedEvent event) {

        System.out.println();
        System.out.println("======================================");
        System.out.println("ORDER-SERVICE received PAYMENT_PROCESSED");
        System.out.println("OrderId       : " + event.getOrderId());
        System.out.println("PaymentStatus : " + event.getPaymentStatus());
        System.out.println("======================================");

        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Order not found: " + event.getOrderId()
                        )
                );

        if ("COMPLETED".equals(event.getPaymentStatus())) {

            order.setStatus("COMPLETED");

        } else if ("FAILED".equals(event.getPaymentStatus())) {

            order.setStatus("PAYMENT_FAILED");

        } else {

            throw new IllegalStateException(
                    "Unknown payment status: "
                            + event.getPaymentStatus()
            );
        }

        orderRepository.save(order);

        System.out.println();
        System.out.println("======================================");
        System.out.println("ORDER STATUS UPDATED");
        System.out.println("OrderId     : " + order.getId());
        System.out.println("New Status  : " + order.getStatus());
        System.out.println("======================================");
    }
}