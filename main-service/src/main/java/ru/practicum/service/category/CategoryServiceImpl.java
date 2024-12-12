package ru.practicum.service.category;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.dao.CategoryRepository;
import ru.practicum.dao.EventRepository;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.CategoryRequest;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.model.Category;

import java.util.Objects;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    CategoryRepository categoryRepository;
    EventRepository eventRepository;

    @Override
    public CategoryDto createCategory(CategoryRequest categoryRequest) {
        if (categoryRepository.existsByName(categoryRequest.getName())) {
            throw new ConflictException("Эта категория уже существует!");
        }
        return CategoryMapper.toCategoryDto(categoryRepository.save(CategoryMapper.toCategory(categoryRequest)));
    }

    @Override
    public CategoryDto updateCategory(Long id, CategoryRequest categoryRequest) {
        Category category = getCategoryById(id);
        if (Objects.nonNull(categoryRequest.getName())) {
            category.setName(categoryRequest.getName());
        }

        if (categoryRepository.existsByName(categoryRequest.getName())) {
            throw new ConflictException("Категория уже существует!");
        }
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);
        if (eventRepository.existsByCategory(category)) {
            throw new ConflictException("Категория не должна быть связана с событием!");
        }
        categoryRepository.deleteById(id);
    }

    private Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Категория с id = " + id + " была не найдена!"));
    }
}
