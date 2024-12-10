package ru.practicum.service.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.constants.Constants;
import ru.practicum.dao.CategoryRepository;
import ru.practicum.dao.EventRepository;
import ru.practicum.dto.event.EventAdmin;
import ru.practicum.dto.event.EventDto;
import ru.practicum.dto.event.UpdateEventDto;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Category;
import ru.practicum.model.Event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class AdminEventServiceImpl implements AdminEventService {
    EventRepository eventRepository;
    CategoryRepository categoryRepository;

    @Override
    public List<EventDto> getEventsWithParam(EventAdmin eventAdmin) {
        LocalDateTime start = null;
        LocalDateTime end = null;

        if (Objects.nonNull(eventAdmin.getStart())) {
            start = LocalDateTime.parse(eventAdmin.getStart(), DateTimeFormatter.ofPattern(Constants.DATE_PATTERN));
        }
        if (Objects.nonNull(eventAdmin.getEnd())) {
            end = LocalDateTime.parse(eventAdmin.getEnd(), DateTimeFormatter.ofPattern(Constants.DATE_PATTERN));
        }

        Pageable pageable = PageRequest.of(eventAdmin.getFrom() / eventAdmin.getSize(), eventAdmin.getSize());

        List<Event> events;
        if (eventAdmin.getUsers() != null || eventAdmin.getStates() != null || eventAdmin.getCategories() != null || start != null || end != null) {
            events = eventRepository.findAllByParams(
                    eventAdmin.getUsers(),
                    eventAdmin.getStates(),
                    eventAdmin.getCategories(),
                    start,
                    end,
                    pageable
            );
        } else {
            events = eventRepository.findAllEvents(pageable);
        }

        return events != null ? events.stream().map(EventMapper::toEventDto).collect(Collectors.toList()) : Collections.emptyList();
    }

    @Override
    public EventDto updateEventByAdmin(Long eventId, UpdateEventDto updateEventDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id = " + eventId + " не было найдено!"));
        Category category = updateEventDto.getCategory() == null ? event.getCategory()
                : categoryRepository.findById(updateEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Категория с id = " + updateEventDto.getCategory() + " не была найдена!"));
        event = eventRepository.save(EventMapper.toUpdatedEvent(updateEventDto, category, event));
        return EventMapper.toEventDto(event);
    }
}
