// src/main/java/com/example/ecommerce/controller/PaymentController.java
package com.example.ecommerce.controller;

import com.example.ecommerce.service.OrderSagaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final OrderSagaService saga;

    @PostMapping("/success/{orderId}")
    public ResponseEntity<Map<String, Object>> paymentSuccess(
            @PathVariable Long orderId,
            @RequestParam(required=false) String tx) {

        String txId = (tx == null ? UUID.randomUUID().toString() : tx);
        saga.handlePaymentSuccess(orderId, txId);

        Map<String, Object> response = new HashMap<>();
        response.put("orderId", orderId);
        response.put("transactionId", txId);
        response.put("status", "PAID");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/failure/{orderId}")
    public ResponseEntity<Map<String, Object>> paymentFailure(
            @PathVariable Long orderId,
            @RequestParam(required=false) String reason) {

        String r = (reason == null ? "simulated-failure" : reason);
        saga.handlePaymentFailure(orderId, r);

        Map<String, Object> response = new HashMap<>();
        response.put("orderId", orderId);
        response.put("status", "CANCELLED");
        response.put("reason", r);

        return ResponseEntity.ok(response);
    }

}
