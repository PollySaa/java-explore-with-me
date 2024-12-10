package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.CompilationRequest;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;

import java.util.Set;

@UtilityClass
public class CompilationMapper {

    public static CompilationDto toCompilationDto(Compilation collection) {
        return CompilationDto.builder()
                .events(collection.getEvents().stream()
                        .map(EventMapper::toEventShortDto)
                        .toList())
                .id(collection.getId())
                .pinned(collection.getPinned())
                .title(collection.getTitle())
                .build();
    }

    public static Compilation toCompilation(CompilationRequest compilationRequest, Set<Event> events) {
        return Compilation.builder()
                .events(events)
                .pinned(compilationRequest.getPinned() == null ? Boolean.FALSE : compilationRequest.getPinned())
                .title(compilationRequest.getTitle())
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
