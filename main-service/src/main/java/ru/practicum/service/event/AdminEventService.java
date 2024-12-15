package ru.practicum.service.event;

import ru.practicum.dto.event.EventDto;
import ru.practicum.dto.event.State;
import ru.practicum.dto.event.UpdateEventDto;

import java.util.List;

public interface AdminEventService {

    List<EventDto> getEventsWithParam(List<Long> users, List<State> states, List<Long> categories,
                                      String rangeStart, String rangeEnd, Integer from, Integer size);

    EventDto updateEventByAdmin(Long eventId, UpdateEventDto updateEventDto);
}
