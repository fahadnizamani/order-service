package com.example.order_service.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class OrderEventProducer {

    private static final String ORDER_CREATED_TOPIC = "order-created";

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    public OrderEventProducer(
            KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public CompletableFuture<SendResult<String, OrderCreatedEvent>>
    sendOrderCreatedEvent(OrderCreatedEvent event) {

        System.out.println(
                "Publishing ORDER_CREATED event. orderId="
                        + event.getOrderId()
                        + ", eventId="
                        + event.getEventId()
        );

        return kafkaTemplate.send(
                ORDER_CREATED_TOPIC,
                String.valueOf(event.getOrderId()),
                event
        );
    }
}