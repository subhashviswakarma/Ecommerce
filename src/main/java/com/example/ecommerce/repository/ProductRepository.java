package com.example.ecommerce.repository;

import com.example.ecommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Search products by name
    List<Product> findByNameContainingIgnoreCase(String name);

    // Get all products under a category
    List<Product> findByCategoryId(Long categoryId);

    // Check stock before operations (optional but recommended)
    boolean existsByIdAndStockGreaterThan(Long id, Integer stock);
}
