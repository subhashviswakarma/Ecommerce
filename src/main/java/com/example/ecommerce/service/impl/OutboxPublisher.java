// src/main/java/com/example/ecommerce/service/impl/OutboxPublisher.java
package com.example.ecommerce.service.impl;

import com.example.ecommerce.model.OutboxEvent;
import com.example.ecommerce.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxRepository outboxRepo;

    // every 5 seconds (adjust in production)
    @Scheduled(fixedDelay = 10000000)
    public void publishPending() {
        List<OutboxEvent> pending = outboxRepo.findByStatusOrderByCreatedAtAsc("PENDING");
        for (OutboxEvent ev : pending) {
            try {
                // Replace this with a real broker publish implementation (Kafka, RabbitMQ)
                System.out.println("[OUTBOX PUBLISH] type=" + ev.getType() + " aggregate=" + ev.getAggregateType() + ":" + ev.getAggregateId());
                System.out.println("payload=" + ev.getPayload());

                ev.setPublishedAt(OffsetDateTime.now());
                ev.setStatus("SENT");
                outboxRepo.save(ev);
            } catch (Exception ex) {
                ev.setStatus("FAILED");
                outboxRepo.save(ev);
            }
        }
    }
}
