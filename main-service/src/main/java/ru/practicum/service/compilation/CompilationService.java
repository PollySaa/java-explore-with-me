package ru.practicum.service.compilation;

import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.CompilationRequest;
import ru.practicum.dto.compilation.NewCompilation;

public interface CompilationService {

    CompilationDto createCompilation(NewCompilation newCompilation);

    CompilationDto updateCompilation(Long id, CompilationRequest collectionRequest);

    void deleteCompilation(Long id);
}
