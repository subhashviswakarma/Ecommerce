package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.CategoryRequest;
import com.example.ecommerce.dto.CategoryResponse;
import com.example.ecommerce.model.Category;
import com.example.ecommerce.repository.CategoryRepository;
import com.example.ecommerce.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repo;

    private CategoryResponse map(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }

    @Override
    public CategoryResponse create(CategoryRequest request) {

        if (repo.existsByName(request.getName())) {
            throw new RuntimeException("Category already exists");
        }

        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        return map(repo.save(category));
    }

    @Override
    public CategoryResponse update(Long id, CategoryRequest request) {

        Category category = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        category.setName(request.getName());
        category.setDescription(request.getDescription());

        return map(repo.save(category));
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }

    @Override
    public CategoryResponse getById(Long id) {
        Category category = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return map(category);
    }

    @Override
    public List<CategoryResponse> getAll() {
        return repo.findAll()
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }
}
