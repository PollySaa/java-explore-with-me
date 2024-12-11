package ru.practicum.controller.adminapi;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.CompilationRequest;
import ru.practicum.service.compilation.CompilationService;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/admin/compilations")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationController {
    CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCollection(@RequestBody CompilationRequest compilationRequest) {
        log.info("Выполнение createCategory");
        return compilationService.createCompilation(compilationRequest);
    }

    @PatchMapping("/{comp-id}")
    public CompilationDto updateCompilation(@PathVariable("comp-id") Long id,
                                            @RequestBody CompilationRequest collectionRequest) {
        log.info("Выполнение updateCompilation");
        return compilationService.updateCompilation(id, collectionRequest);
    }

    @DeleteMapping("/{comp-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable("comp-id") Long id) {
        log.info("Выполнение deleteCompilation");
        compilationService.deleteCompilation(id);
    }
}
