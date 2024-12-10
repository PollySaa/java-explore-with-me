package ru.practicum.controller.publicapi;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.service.compilation.PublicCompilationService;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/compilations")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PublicCompilationController {
    PublicCompilationService publicCompilationService;

    @GetMapping
    public List<CompilationDto> getCompilationsByPublicUser(@RequestParam(required = false) Boolean pinned,
                                                            @RequestParam Integer from,
                                                            @RequestParam Integer size) {
        log.info("Выполнение getCompilationsByPublicUser");
        return publicCompilationService.getCompilationsByPublicUser(pinned, from, size);
    }

    @GetMapping("/{comp-id}")
    public CompilationDto getCompilationByIdByPublicUser(@PathVariable("comp-id") Long id) {
        log.info("Выполнение getCompilationByIdByPublicUser");
        return publicCompilationService.getCompilationByIdByPublicUser(id);
    }
}
