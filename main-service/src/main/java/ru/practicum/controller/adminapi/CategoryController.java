package ru.practicum.controller.adminapi;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.CategoryRequest;
import ru.practicum.service.category.CategoryService;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/admin/categories")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryController {
    CategoryService categoryService;

    @PostMapping
    public CategoryDto createCategory(@RequestBody CategoryRequest categoryRequest) {
        log.info("Выполнение createCategory");
        return categoryService.createCategory(categoryRequest);
    }

    @PatchMapping("/{cat-id}")
    public CategoryDto updateCategory(@PathVariable("cat-id") Long id, @RequestBody CategoryRequest categoryRequest) {
        log.info("Выполнение updateCategory");
        return categoryService.updateCategory(id, categoryRequest);
    }

    @DeleteMapping("/{cat-id}")
    public void deleteCategory(@PathVariable("cat-id") Long id) {
        log.info("Выполнение deleteCategory");
        categoryService.deleteCategory(id);
    }
}
