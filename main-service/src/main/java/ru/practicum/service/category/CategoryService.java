package ru.practicum.service.category;

import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.CategoryRequest;

public interface CategoryService {

    CategoryDto createCategory(CategoryRequest categoryRequest);

    CategoryDto updateCategory(Long id, CategoryRequest categoryRequest);

    void deleteCategory(Long id);
}
