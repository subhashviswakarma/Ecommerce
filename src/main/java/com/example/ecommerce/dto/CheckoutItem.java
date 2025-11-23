// src/main/java/com/example/ecommerce/dto/CheckoutItem.java
package com.example.ecommerce.dto;

import lombok.Data;

@Data
public class CheckoutItem {
    private Long productId;
    private Integer quantity;
}
