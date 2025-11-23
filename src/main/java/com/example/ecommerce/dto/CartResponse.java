package com.example.ecommerce.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartResponse {

    private Long id;
    private String productName;
    private Double price;
    private Integer quantity;
    private Double total;
}
