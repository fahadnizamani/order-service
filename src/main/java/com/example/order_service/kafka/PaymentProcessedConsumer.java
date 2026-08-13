package com.example.order_service.kafka;

import com.example.order_service.entity.Order;
import com.example.order_service.entity.ProcessedEvent;
import com.example.order_service.repository.OrderRepository;
import com.example.order_service.repository.ProcessedEventRepository;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class PaymentProcessedConsumer {

    private final OrderRepository orderRepository;
    private final ProcessedEventRepository processedEventRepository;

    public PaymentProcessedConsumer(OrderRepository orderRepository, ProcessedEventRepository processedEventRepository) {
        this.orderRepository = orderRepository;
        this.processedEventRepository = processedEventRepository;
    }

    @RetryableTopic(
            attempts = "4",
            backOff = @BackOff(
                    delay = 2000,
                    multiplier = 2.0
            ),
            dltTopicSuffix = "-dlt"
    )
    @KafkaListener(
            topics = "payment-processed",
            groupId = "order-service"
    )
    @Transactional
    public void consume(PaymentProcessedEvent event) {

        String eventId = event.getEventId();
        if (processedEventRepository.existsByEventId(eventId)) {
            System.out.println(
                    "Duplicate event ignored. eventId=" + eventId
            );
            return;
        }

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

            // Actual payment failure
            order.setStatus("PAYMENT_FAILED");

        } else if ("INVENTORY_FAILED".equals(event.getPaymentStatus())) {

            // Payment succeeded, but inventory failed
            // and payment was compensated/refunded.
            order.setStatus("INVENTORY_FAILED");

        } else {

            throw new IllegalStateException(
                    "Unknown payment status: "
                            + event.getPaymentStatus()
            );
        }

        orderRepository.save(order);

        processedEventRepository.save(
                new ProcessedEvent(
                        event.getEventId(),
                        event.getEventType(),
                        LocalDateTime.now()
                )
        );

        System.out.println();
        System.out.println("======================================");
        System.out.println("ORDER STATUS UPDATED");
        System.out.println("OrderId     : " + order.getId());
        System.out.println("New Status  : " + order.getStatus());
        System.out.println("======================================");
    }

    @DltHandler
    public void handleDlt(PaymentProcessedEvent event) {

        System.out.println();
        System.out.println("======================================");
        System.out.println("PAYMENT_PROCESSED MOVED TO DLT");
        System.out.println("OrderId       : " + event.getOrderId());
        System.out.println("PaymentStatus : " + event.getPaymentStatus());
        System.out.println("======================================");
    }
}