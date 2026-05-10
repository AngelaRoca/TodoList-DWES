package com.Tarea_DWES_AngelaRocaBlanco.TodoList.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.Tarea_DWES_AngelaRocaBlanco.TodoList.dto.CategoryDTOs.CategoryRequest;
import com.Tarea_DWES_AngelaRocaBlanco.TodoList.dto.CategoryDTOs.CategoryResponse;
import com.Tarea_DWES_AngelaRocaBlanco.TodoList.model.Category;
import com.Tarea_DWES_AngelaRocaBlanco.TodoList.repository.CategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public CategoryResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    public CategoryResponse create(CategoryRequest req) {
        if (categoryRepository.existsByTitle(req.getTitle()))
            throw new IllegalArgumentException("Ya existe una categoría con ese título");
        Category category = Category.builder().title(req.getTitle()).build();
        return toResponse(categoryRepository.save(category));
    }

    public CategoryResponse update(Long id, CategoryRequest req) {
        Category category = findOrThrow(id);
        category.setTitle(req.getTitle());
        return toResponse(categoryRepository.save(category));
    }

    public void delete(Long id) {
        categoryRepository.delete(findOrThrow(id));
    }

    private Category findOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría con id " + id + " no encontrada"));
    }

    public CategoryResponse toResponse(Category c) {
        CategoryResponse r = new CategoryResponse();
        r.setId(c.getId());
        r.setTitle(c.getTitle());
        return r;
    }
}
