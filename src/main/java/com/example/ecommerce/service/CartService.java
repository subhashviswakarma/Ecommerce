package com.example.ecommerce.service;

import com.example.ecommerce.dto.CartRequest;
import com.example.ecommerce.dto.CartResponse;

import java.util.List;

public interface CartService {

    CartResponse addToCart(CartRequest request);

    List<CartResponse> getUserCart(Long userId);

    CartResponse updateQuantity(Long cartItemId, int quantity);

    void removeItem(Long cartItemId);
}
