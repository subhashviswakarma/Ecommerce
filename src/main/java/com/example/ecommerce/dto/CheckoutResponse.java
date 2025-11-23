// src/main/java/com/example/ecommerce/dto/CheckoutResponse.java
package com.example.ecommerce.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckoutResponse {
    private Long orderId;
    private String status;
    private Double totalAmount;
}
