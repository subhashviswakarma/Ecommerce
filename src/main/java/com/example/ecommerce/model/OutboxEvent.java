// src/main/java/com/example/ecommerce/model/OutboxEvent.java
package com.example.ecommerce.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "outbox_events")
public class OutboxEvent {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String aggregateType;
    private Long aggregateId;

    private String type;

    @Lob
    private String payload;

    private OffsetDateTime createdAt;
    private OffsetDateTime publishedAt;

    private String status; // PENDING, SENT, FAILED
}
