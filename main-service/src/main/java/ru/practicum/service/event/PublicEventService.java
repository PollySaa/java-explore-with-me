package ru.practicum.service.event;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.dto.event.EventDto;
import ru.practicum.dto.event.EventPublic;
import ru.practicum.dto.event.EventShortDto;

import java.util.List;

public interface PublicEventService {

    List<EventShortDto> getEvents(EventPublic eventPublic, HttpServletRequest request);

    EventDto getEventByIdByPublicUser(Long id, String ip, HttpServletRequest request);
}
