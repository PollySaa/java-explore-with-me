package ru.practicum.service.compilation;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.dao.CompilationRepository;
import ru.practicum.dao.EventRepository;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.CompilationRequest;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    CompilationRepository compilationRepository;
    EventRepository eventRepository;

    @Override
    public CompilationDto createCompilation(CompilationRequest compilationRequest) {
        List<Long> eventIds = compilationRequest.getEvents() != null
                ? compilationRequest.getEvents()
                : Collections.emptyList();

        Set<Event> events = eventIds.stream()
                .map(eventRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());

        Compilation newCompilation = CompilationMapper.toCompilation(compilationRequest, events);
        Compilation savedCompilation = compilationRepository.save(newCompilation);
        return CompilationMapper.toCompilationDto(savedCompilation);
    }

    @Override
    public CompilationDto updateCompilation(Long id, CompilationRequest compilationRequest) {
        try {
            Compilation existingCompilation = compilationRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Compilation with id = " + id + " not found!"));

            List<Long> eventIds = compilationRequest.getEvents() != null
                    ? compilationRequest.getEvents()
                    : Collections.emptyList();

            Set<Event> newEvents = eventIds.stream()
                    .map(eventRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toSet());

            existingCompilation.setEvents(newEvents);
            existingCompilation.setTitle(compilationRequest.getTitle());
            existingCompilation.setPinned(compilationRequest.getPinned() != null ? compilationRequest.getPinned() : existingCompilation.getPinned());


            Compilation savedCompilation = compilationRepository.save(existingCompilation);
            return CompilationMapper.toCompilationDto(savedCompilation);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update compilation", e);
        }
    }

    @Override
    public void deleteCompilation(Long id) {
        if (!compilationRepository.existsById(id)) {
            throw new NotFoundException("Собрание с id = " + id + " не было найдено!");
        }

        compilationRepository.deleteById(id);
    }
}
