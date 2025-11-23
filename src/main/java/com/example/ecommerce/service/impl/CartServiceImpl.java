package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.CartRequest;
import com.example.ecommerce.dto.CartResponse;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepo;
    private final UserRepository userRepo;
    private final ProductRepository productRepo;

    private CartResponse mapToResponse(CartItem item) {
        return CartResponse.builder()
                .id(item.getId())
                .productName(item.getProduct().getName())
                .price(item.getProduct().getPrice())
                .quantity(item.getQuantity())
                .total(item.getQuantity() * item.getProduct().getPrice())
                .build();
    }

    @Override
    public CartResponse addToCart(CartRequest request) {

        User user = userRepo.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepo.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Load all items of this user
        List<CartItem> userCart = cartRepo.findByUserId(request.getUserId());

        // Check if product already exists in cart
        CartItem item = userCart.stream()
                .filter(i -> i.getProduct().getId().equals(request.getProductId()))
                .findFirst()
                .map(existing -> {
                    existing.setQuantity(existing.getQuantity() + request.getQuantity());
                    return existing;
                })
                .orElseGet(() -> CartItem.builder()
                        .user(user)
                        .product(product)
                        .quantity(request.getQuantity())
                        .build());

        return mapToResponse(cartRepo.save(item));
    }

    @Override
    public List<CartResponse> getUserCart(Long userId) {

        // Just load by ID — no need to load User entity
        return cartRepo.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CartResponse updateQuantity(Long cartItemId, int quantity) {

        CartItem item = cartRepo.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        item.setQuantity(quantity);

        return mapToResponse(cartRepo.save(item));
    }

    @Override
    public void removeItem(Long cartItemId) {
        cartRepo.deleteById(cartItemId);
    }
}
