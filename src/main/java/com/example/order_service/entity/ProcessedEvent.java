package com.example.order_service.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "processed_events",
        indexes = {
                @Index(
                        name = "idx_processed_events_event_id",
                        columnList = "event_id",
                        unique = true
                )
        }
)
public class ProcessedEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false, unique = true, length = 100)
    private String eventId;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Column(name = "processed_at", nullable = false)
    private LocalDateTime processedAt;

    public ProcessedEvent() {
    }

    public ProcessedEvent(
            String eventId,
            String eventType,
            LocalDateTime processedAt) {

        this.eventId = eventId;
        this.eventType = eventType;
        this.processedAt = processedAt;
    }

    public Long getId() {
        return id;
    }

    public String getEventId() {
        return eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }
}
