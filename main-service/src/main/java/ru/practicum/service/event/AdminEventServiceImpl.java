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
import ru.practicum.dao.LocationRepository;
import ru.practicum.dto.event.EventDto;
import ru.practicum.dto.event.State;
import ru.practicum.dto.event.UpdateEventDto;
import ru.practicum.dto.location.LocationDto;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.LocationMapper;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.Location;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class AdminEventServiceImpl implements AdminEventService {
    EventRepository eventRepository;
    CategoryRepository categoryRepository;
    LocationRepository locationRepository;

    @Override
    public List<EventDto> getEventsWithParam(List<Long> users, List<State> states, List<Long> categories,
                                             String rangeStart, String rangeEnd, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size);
        LocalDateTime start = null;
        LocalDateTime end = null;

        if (rangeStart != null) {
            start = LocalDateTime.parse(URLDecoder.decode(rangeStart, StandardCharsets.UTF_8), Constants.DATE_TIME_FORMATTER);
        }
        if (rangeEnd != null) {
            end = LocalDateTime.parse(URLDecoder.decode(rangeEnd, StandardCharsets.UTF_8), Constants.DATE_TIME_FORMATTER);
        }
        List<Event> events;
        if (start != null && end != null) {
            if (end.isBefore(start) || end.equals(start)) {
                throw new ConflictException("Даты не могут быть равны или дата окончания не может быть раньше даты начала");
            }
            events = eventRepository.findAllEventsByFilterAndPeriod(users, states, categories, start, end, pageable);
        } else if (start != null) {
            events = eventRepository.findAllEventsByFilterAndRangeStart(users, states, categories, start, pageable);
        } else if (end != null) {
            events = eventRepository.findAllEventsByFilterAndRangeEnd(users, states, categories, end, pageable);
        } else {
            events = eventRepository.findAllByParams(users, states, categories, pageable);
        }
        return events.stream()
                .map(EventMapper::toEventDto)
                .toList();
    }

    @Override
    public EventDto updateEventByAdmin(Long eventId, UpdateEventDto updateEventDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id = " + eventId + " не было найдено!"));

        if (!event.getState().equals(State.PENDING)) {
            throw new ConflictException("Событие должно быть в состоянии ожидания публикации!");
        }

        if (updateEventDto.getEventDate() != null) {
            LocalDateTime eventDate = updateEventDto.getEventDate();
            if (eventDate.isBefore(LocalDateTime.now().plusHours(1))) {
                throw new ConflictException("Дата начала события должна быть не ранее чем за час от текущего времени!");
            }
        }

        Category category = updateEventDto.getCategory() == null ? event.getCategory()
                : categoryRepository.findById(updateEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Категория с id = " + updateEventDto.getCategory() + " не была найдена!"));

        if (updateEventDto.getLocation() != null) {
            LocationDto locationDto = updateEventDto.getLocation();
            Location location = LocationMapper.toLocation(locationDto);
            if (location.getId() == null) {
                location = locationRepository.save(location);
            }
            event.setLocation(location);
        }
        event = eventRepository.save(EventMapper.toUpdatedEvent(updateEventDto, category, event));
        return EventMapper.toEventDto(event);
    }
}