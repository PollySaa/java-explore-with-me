package ru.practicum.service.compilation;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.constants.Constants;
import ru.practicum.dao.CompilationRepository;
import ru.practicum.dao.EventRepository;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.CompilationRequest;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;

import java.util.*;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    CompilationRepository compilationRepository;
    EventRepository eventRepository;

    @Override
    public CompilationDto createCompilation(CompilationRequest compilationRequest) {
        Set<Event> events = new HashSet<>();
        if (compilationRequest.getEvents() != null) {
            events = eventRepository.findEventsByIdIn(compilationRequest.getEvents(), Constants.ORDER_BY_EVENT_DAY);
        }
        Compilation newCompilation = CompilationMapper.toCompilation(compilationRequest, events);
        Compilation savedCompilation = compilationRepository.save(newCompilation);
        return CompilationMapper.toCompilationDto(savedCompilation);
    }

    @Override
    public CompilationDto updateCompilation(Long id, CompilationRequest compilationRequest) {
        Compilation existingCompilation = compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Compilation with id = " + id + " not found!"));

        Set<Event> newEvents = new TreeSet<>(Comparator.comparing(Event::getEventDate));
        newEvents.addAll(existingCompilation.getEvents());
        if (compilationRequest.getEvents() != null) {
            newEvents.addAll(eventRepository.findEventsByIdIn(compilationRequest.getEvents(), Constants.ORDER_BY_EVENT_DAY));
        }
        existingCompilation = compilationRepository
                .save(CompilationMapper.toUpdateCompilation(compilationRequest, existingCompilation, newEvents));

        return CompilationMapper.toCompilationDto(existingCompilation);
    }

    @Override
    public void deleteCompilation(Long id) {
        if (!compilationRepository.existsById(id)) {
            throw new NotFoundException("Compilation with id = " + id + " not found!");
        }

        compilationRepository.deleteById(id);
    }
}
