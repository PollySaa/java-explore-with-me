package ru.practicum.service.compilation;

import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.CompilationRequest;

public interface CompilationService {

    CompilationDto createCompilation(CompilationRequest collectionRequest);

    CompilationDto updateCompilation(Long id, CompilationRequest collectionRequest);

    void deleteCompilation(Long id);
}
