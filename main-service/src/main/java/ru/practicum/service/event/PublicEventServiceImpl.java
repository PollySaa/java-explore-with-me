package ru.practicum.service.event;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.constants.Constants;
import ru.practicum.dao.EventRepository;
import ru.practicum.dto.event.EventDto;
import ru.practicum.dto.event.EventPublic;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.State;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Event;
import ru.practicum.stats.StatsClient;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@ComponentScan
@Service
@RequiredArgsConstructor
public class PublicEventServiceImpl implements PublicEventService {
    private final EventRepository eventRepository;
    private final StatsClient statsClient;

    @Override
    public List<EventShortDto> getEvents(EventPublic eventPublic, HttpServletRequest request) {
        Pageable pageable;
        if (eventPublic.getSort() != null) {
            pageable = PageRequest.of(
                    eventPublic.getFrom() > 0 ? eventPublic.getFrom() / eventPublic.getSize() : 0,
                    eventPublic.getSize(),
                    Sort.by(eventPublic.getSort()).descending() // Сортировка по переданному полю
            );
        } else {
            pageable = PageRequest.of(
                    eventPublic.getFrom() > 0 ? eventPublic.getFrom() / eventPublic.getSize() : 0,
                    eventPublic.getSize()
            );
        }

        LocalDateTime startDate = null;
        if (eventPublic.getRangeStart() != null) {
            startDate = LocalDateTime.parse(URLDecoder.decode(eventPublic.getRangeStart(), StandardCharsets.UTF_8));
        } else {
            startDate = LocalDateTime.now();
        }

        LocalDateTime endDate = null;
        if (eventPublic.getRangeEnd() != null) {
            endDate = LocalDateTime.parse(URLDecoder.decode(eventPublic.getRangeEnd(), StandardCharsets.UTF_8));
        }

        if (endDate != null && (endDate.isBefore(startDate) || endDate.equals(startDate))) {
            throw new ConflictException("Даты не могут быть равны или дата окончания не может быть раньше даты начала");
        }

        List<Event> events;
        if (endDate != null) {
            events = eventRepository.findAllPublishedEventsByFilterAndPeriod(
                    eventPublic.getText(), eventPublic.getCategories(), eventPublic.getPaid(),
                    startDate, endDate, eventPublic.getOnlyAvailable(), pageable);
        } else {
            events = eventRepository.findAllPublishedEventsByFilterAndRangeStart(
                    eventPublic.getText(), eventPublic.getCategories(), eventPublic.getPaid(),
                    startDate, eventPublic.getOnlyAvailable(), pageable);
        }

        statsClient.createHit(createEndpointHitDto(request));

        return events.stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventDto getEventByIdByPublicUser(Long id, HttpServletRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Событие с id = " + id + " не было найдено!"));

        if (!event.getState().equals(State.PUBLISHED)) {
            throw new NotFoundException("Можно смотреть только опубликованные события!");
        }

        String start = event.getCreatedOn().withNano(0).format(DateTimeFormatter
                .ofPattern(Constants.DATE_PATTERN));
        String end = event.getEventDate().withNano(0).format(DateTimeFormatter
                .ofPattern(Constants.DATE_PATTERN));
        List<ViewStatsDto> viewStatsDtoList = statsClient.getStatsByDateAndUris(start, end,
                List.of(request.getRequestURI()), true);

        if (!viewStatsDtoList.isEmpty()) {
            event.setViews(viewStatsDtoList.getFirst().getHits());
        }
        statsClient.createHit(createEndpointHitDto(request));
        return EventMapper.toEventDto(event);
    }

    @Transactional
    private EndpointHitDto createEndpointHitDto(HttpServletRequest request) {
        return EndpointHitDto.builder()
                .app("ewm-main-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now().withNano(0).format(DateTimeFormatter
                        .ofPattern(Constants.DATE_PATTERN)))
                .build();
    }
}
