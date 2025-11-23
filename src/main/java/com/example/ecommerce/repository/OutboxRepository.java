// src/main/java/com/example/ecommerce/repository/OutboxRepository.java
package com.example.ecommerce.repository;

import com.example.ecommerce.model.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxEvent, Long> {
    List<OutboxEvent> findByStatusOrderByCreatedAtAsc(String status);
}
