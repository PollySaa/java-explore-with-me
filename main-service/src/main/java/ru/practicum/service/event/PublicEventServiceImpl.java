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
import ru.practicum.dto.event.*;
import ru.practicum.exceptions.IncorrectParameterException;
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
    public List<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid, String rangeStart, String rangeEnd,
                                         Boolean onlyAvailable, String sort, Integer from, Integer size,
                                         HttpServletRequest request) {
        Pageable pageable;
        if (sort != null) {
            String sortField = sort.equals(SortType.EVENT_DATE.name()) ? "eventDate" : "views";
            pageable = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by(sortField).descending());
        } else {
            pageable = PageRequest.of(from > 0 ? from / size : 0, size);
        }
        LocalDateTime startDate = rangeStart != null
                ? LocalDateTime.parse(URLDecoder.decode(rangeStart, StandardCharsets.UTF_8), Constants.DATE_TIME_FORMATTER)
                : LocalDateTime.now();
        LocalDateTime endDate = null;
        if (rangeEnd != null) {
            endDate = LocalDateTime.parse(URLDecoder.decode(rangeEnd, StandardCharsets.UTF_8), Constants.DATE_TIME_FORMATTER);
        }
        List<Event> events;
        if (endDate != null) {
            if (endDate.isBefore(startDate) || endDate.equals(startDate)) {
                throw new IncorrectParameterException("Даты не могут быть равны или дата окончания не может быть раньше даты начала");
            }
            events = eventRepository.findAllPublishedEventsByFilterAndPeriod(text, categories, paid, startDate, endDate, onlyAvailable, pageable);
        } else {
            events = eventRepository.findAllPublishedEventsByFilterAndRangeStart(text, categories, paid, startDate, onlyAvailable, pageable);
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
