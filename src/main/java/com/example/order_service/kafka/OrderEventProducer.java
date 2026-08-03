package com.example.order_service.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventProducer {

    private static final String ORDER_CREATED_TOPIC = "order-created";

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    public OrderEventProducer(
            KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOrderCreatedEvent(OrderCreatedEvent event) {

        String key = event.getOrderId().toString();

        System.out.println(
                ">>> Publishing ORDER_CREATED event. orderId="
                        + event.getOrderId()
                        + ", eventId="
                        + event.getEventId()
        );

        kafkaTemplate.send(
                ORDER_CREATED_TOPIC,
                key,
                event
        );
    }
}