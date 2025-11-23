package com.example.ecommerce.dto;

import lombok.Data;

@Data
public class CartRequest {
    private Long userId;
    private Long productId;
    private Integer quantity;
}
