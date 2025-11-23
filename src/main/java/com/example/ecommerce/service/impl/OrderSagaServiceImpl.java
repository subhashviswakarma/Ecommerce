package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.CheckoutRequest;
import com.example.ecommerce.dto.CheckoutResponse;
import com.example.ecommerce.model.*;
import com.example.ecommerce.repository.*;
import com.example.ecommerce.service.OrderSagaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderSagaServiceImpl implements OrderSagaService {

    private final ProductRepository productRepo;
    private final OrderRepository orderRepo;
    private final CartRepository cartRepo;
    private final OutboxRepository outboxRepo;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public CheckoutResponse checkout(CheckoutRequest request) {

        // 1. Fetch cart items for this user
        List<CartItem> cartItems = cartRepo.findByUserId(request.getUserId());

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // 2. Convert CartItems -> OrderItems + Reserve stock
        double total = 0.0;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem ci : cartItems) {

            Product p = productRepo.findById(ci.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + ci.getProduct().getId()));

            if (p.getStock() < ci.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + p.getName());
            }

            // Decrement stock (optimistic lock safe)
            p.setStock(p.getStock() - ci.getQuantity());
            productRepo.save(p);

            OrderItem oi = OrderItem.builder()
                    .productId(p.getId())
                    .productName(p.getName())
                    .quantity(ci.getQuantity())
                    .unitPrice(p.getPrice())
                    .totalPrice(p.getPrice() * ci.getQuantity())
                    .build();

            total += oi.getTotalPrice();
            orderItems.add(oi);
        }
        String key = request.getIdempotencyKey();
        if (key == null || key.isBlank()) {
            key = UUID.randomUUID().toString();
        }

        // 3. Create order with RESERVED status
        Order order = Order.builder()
                .userId(request.getUserId())
                .status(OrderStatus.RESERVED)
                .idempotencyKey(request.getIdempotencyKey())
                .totalAmount(total)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .items(orderItems)
                .build();

        orderRepo.save(order);

        // 4. Delete cart items after reservation
        cartRepo.deleteByUserId(request.getUserId());

        // 5. Create Outbox event for OrderCreated
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("orderId", order.getId());
            payload.put("userId", order.getUserId());
            payload.put("total", order.getTotalAmount());
            payload.put("items", order.getItems());

            String json = objectMapper.writeValueAsString(payload);

            OutboxEvent ev = OutboxEvent.builder()
                    .aggregateType("Order")
                    .aggregateId(order.getId())
                    .type("OrderCreated")
                    .payload(json)
                    .createdAt(OffsetDateTime.now())
                    .status("PENDING")
                    .build();

            outboxRepo.save(ev);

        } catch (Exception e) {
            throw new RuntimeException("Outbox event creation failed", e);
        }

        return CheckoutResponse.builder()
                .orderId(order.getId())
                .status(order.getStatus().name())
                .totalAmount(order.getTotalAmount())
                .build();
    }

    @Override
    @Transactional
    public void handlePaymentSuccess(Long orderId, String tx) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(OrderStatus.PAID);
        order.setUpdatedAt(OffsetDateTime.now());
        orderRepo.save(order);
    }

    @Override
    @Transactional
    public void handlePaymentFailure(Long orderId, String reason) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Compensation: restore stock
        for (OrderItem item : order.getItems()) {
            Product p = productRepo.findById(item.getProductId())
                    .orElseThrow();

            p.setStock(p.getStock() + item.getQuantity());
            productRepo.save(p);
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(OffsetDateTime.now());
        orderRepo.save(order);
    }
}
