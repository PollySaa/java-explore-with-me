package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.CompilationRequest;
import ru.practicum.dto.compilation.NewCompilation;
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

    public static Compilation toCompilation(NewCompilation compilation, Set<Event> events) {
        return Compilation.builder()
                .events(events)
                .title(compilation.getTitle())
                .pinned(compilation.getPinned() == null ? Boolean.FALSE : compilation.getPinned())
                .build();
    }

    public static Compilation toUpdateCompilation(CompilationRequest updateCompilationRequest,
                                                  Compilation oldCompilation,
                                                  Set<Event> newEvents) {
        return Compilation.builder()
                .id(oldCompilation.getId())
                .events(newEvents)
                .pinned(updateCompilationRequest.getPinned() == null ? oldCompilation.getPinned()
                        : updateCompilationRequest.getPinned())
                .title(updateCompilationRequest.getTitle() == null ? oldCompilation.getTitle()
                        : updateCompilationRequest.getTitle())
                .build();
    }
}
