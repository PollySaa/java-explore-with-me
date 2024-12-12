package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.CompilationRequest;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;

import java.util.Set;

@UtilityClass
public class CompilationMapper {

    public static CompilationDto toCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.getPinned())
                .events(compilation.getEvents().stream()
                        .map(EventMapper::toEventShortDto)
                        .toList())
                .build();
    }

    public static Compilation toCompilation(CompilationRequest compilationRequest, Set<Event> events) {
        return Compilation.builder()
                .events(events)
                .title(compilationRequest.getTitle())
                .pinned(compilationRequest.getPinned() == null ? Boolean.FALSE : compilationRequest.getPinned())
                .build();
    }
}
