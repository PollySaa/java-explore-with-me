package ru.practicum.service.event;

import ru.practicum.dto.event.EventAdmin;
import ru.practicum.dto.event.EventDto;
import ru.practicum.dto.event.UpdateEventDto;

import java.util.List;

public interface AdminEventService {

    List<EventDto> getEventsWithParam(EventAdmin eventAdmin);

    EventDto updateEventByAdmin(Long eventId, UpdateEventDto updateEventDto);
}
