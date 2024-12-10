package ru.practicum.service.category;

import ru.practicum.dto.category.CategoryDto;

import java.util.List;

public interface PublicCategoryService {

    List<CategoryDto> getCategoriesByPublicUser(Integer from, Integer size);

    CategoryDto getCategoryByIdByPublicUser(Long id);
}
