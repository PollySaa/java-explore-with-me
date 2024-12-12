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
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Event;
import ru.practicum.stats.StatsClient;

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
        LocalDateTime rangeStart = eventPublic.getRangeStart() != null ?
                LocalDateTime.parse(eventPublic.getRangeStart(), DateTimeFormatter.ofPattern(Constants.DATE_PATTERN)) :
                null;
        LocalDateTime rangeEnd = eventPublic.getRangeEnd() != null ?
                LocalDateTime.parse(eventPublic.getRangeEnd(), DateTimeFormatter.ofPattern(Constants.DATE_PATTERN)) :
                null;

        Sort sort = Sort.unsorted();
        if (eventPublic.getSort() != null) {
            String sortField = eventPublic.getSort().equals("EVENT_DATE") ? "eventDate" : "views";
            sort = Sort.by(sortField).descending();
        }

        Pageable pageable = PageRequest.of(eventPublic.getFrom() / eventPublic.getSize(),
                eventPublic.getSize(), sort);
        List<Event> events;
        if (rangeEnd != null) {
            events = eventRepository.findAllPublishedEventsByFilterAndPeriod(
                    eventPublic.getText(), eventPublic.getCategories(), eventPublic.getPaid(),
                    rangeStart, rangeEnd, eventPublic.getOnlyAvailable(), pageable);
        } else {
            events = eventRepository.findAllPublishedEventsByFilterAndRangeStart(
                    eventPublic.getText(), eventPublic.getCategories(), eventPublic.getPaid(),
                    rangeStart, eventPublic.getOnlyAvailable(), pageable);
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
