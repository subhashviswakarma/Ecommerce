package com.example.ecommerce.dto;

import lombok.Data;

@Data
public class CheckoutRequest {
    private Long userId;
    private String idempotencyKey; // optional but useful
}
