// src/main/java/com/example/ecommerce/service/OrderSagaService.java
package com.example.ecommerce.service;

import com.example.ecommerce.dto.CheckoutRequest;
import com.example.ecommerce.dto.CheckoutResponse;

public interface OrderSagaService {
    CheckoutResponse checkout(CheckoutRequest request);
    void handlePaymentSuccess(Long orderId, String transactionId);
    void handlePaymentFailure(Long orderId, String reason);
}
