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

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    CompilationRepository collectionRepository;
    EventRepository eventRepository;

    @Override
    public CompilationDto createCompilation(CompilationRequest collectionRequest) {
        Set<Event> events = collectionRequest.getEvents().stream()
                .map(eventRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());

        Compilation newCollection = CompilationMapper.toCompilation(collectionRequest, events);
        Compilation savedCollection = collectionRepository.save(newCollection);
        return CompilationMapper.toCompilationDto(savedCollection);
    }

    @Override
    public CompilationDto updateCompilation(Long id, CompilationRequest collectionRequest) {
        Compilation existingCollection = collectionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Собрание с id = " + id + " не было найдено!"));

        Set<Event> newEvents = collectionRequest.getEvents().stream()
                .map(eventRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());

        Compilation updatedCollection = CompilationMapper.toUpdateCompilation(collectionRequest, existingCollection, newEvents);
        Compilation savedCollection = collectionRepository.save(updatedCollection);
        return CompilationMapper.toCompilationDto(savedCollection);
    }

    @Override
    public void deleteCompilation(Long id) {
        if (!collectionRepository.existsById(id)) {
            throw new NotFoundException("Собрание с id = " + id + " не было найдено!");
        }

        collectionRepository.deleteById(id);
    }
}
