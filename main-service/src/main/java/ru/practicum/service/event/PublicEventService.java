package ru.practicum.service.event;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.dto.event.EventDto;
import ru.practicum.dto.event.EventShortDto;

import java.time.LocalDateTime;
import java.util.List;

public interface PublicEventService {

    List<EventShortDto> getEvents(LocalDateTime rangeStart, LocalDateTime rangeEnd, String text,
                                  List<Long> categories, Boolean paid, Boolean onlyAvailable, String sort, Integer from,
                                  Integer size, HttpServletRequest request);

    EventDto getEventByIdByPublicUser(Long id, HttpServletRequest request);
}
