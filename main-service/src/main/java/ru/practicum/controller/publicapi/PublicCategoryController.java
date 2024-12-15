package ru.practicum.controller.publicapi;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.service.category.PublicCategoryService;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/categories")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PublicCategoryController {
    PublicCategoryService publicCategoryService;

    @GetMapping
    public List<CategoryDto> getCategoriesByPublicUser(@RequestParam(defaultValue = "0") Integer from,
                                                       @RequestParam(defaultValue = "10") Integer size) {
        log.info("Выполнение getCategoriesByPublicUser");
        return publicCategoryService.getCategoriesByPublicUser(from, size);
    }

    @GetMapping("/{cat-id}")
    public CategoryDto getCategoryByIdByPublicUser(@PathVariable("cat-id") Long id) {
        log.info("Выполнение getCategoryByIdByPublicUser");
        return publicCategoryService.getCategoryByIdByPublicUser(id);
    }
}
