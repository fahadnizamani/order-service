package com.example.order_service.service;

import com.example.order_service.entity.OutboxEvent;
import com.example.order_service.kafka.OrderCreatedEvent;
import com.example.order_service.kafka.OrderEventProducer;
import com.example.order_service.repository.OutboxEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class OutboxPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final OrderEventProducer orderEventProducer;
    private final ObjectMapper objectMapper;

    public OutboxPublisher(
            OutboxEventRepository outboxEventRepository,
            OrderEventProducer orderEventProducer,
            ObjectMapper objectMapper) {

        this.outboxEventRepository = outboxEventRepository;
        this.orderEventProducer = orderEventProducer;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelay = 1000)
    public void publishOutboxEvents() {

        List<OutboxEvent> events =
                outboxEventRepository
                        .findTop100ByPublishedFalseOrderByCreatedAtAsc();

        for (OutboxEvent outboxEvent : events) {

            try {

                if ("ORDER_CREATED".equals(outboxEvent.getEventType())) {

                    OrderCreatedEvent event =
                            objectMapper.readValue(
                                    outboxEvent.getPayload(),
                                    OrderCreatedEvent.class
                            );

                    orderEventProducer
                            .sendOrderCreatedEvent(event)
                            .get(10, TimeUnit.SECONDS);

                    outboxEvent.setPublished(true);
                    outboxEvent.setPublishedAt(LocalDateTime.now());

                    outboxEventRepository.save(outboxEvent);

                    System.out.println(
                            ">>> OUTBOX EVENT PUBLISHED. eventId="
                                    + outboxEvent.getEventId()
                    );
                }

            } catch (Exception e) {

                System.err.println(
                        ">>> FAILED TO PUBLISH OUTBOX EVENT. eventId="
                                + outboxEvent.getEventId()
                                + ", error="
                                + e.getMessage()
                );
            }
        }
    }
}