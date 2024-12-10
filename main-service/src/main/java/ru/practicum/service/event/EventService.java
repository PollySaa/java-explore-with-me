package ru.practicum.service.event;

import ru.practicum.dto.event.*;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.dto.request.ResultRequestStatusDto;

import java.util.List;

public interface EventService {

    EventDto createEvent(Long id, NewEventDto newEventDto);

    EventDto updateEvent(Long id, Long eventId, UpdateEventDto updateEventDto);

    EventDto getEventByUserIdAndEventId(Long userId, Long eventId);

    List<EventShortDto> getEventsByUserId(Long userId, Integer from,Integer size);

    List<RequestDto> getRequestsByOwnerEventAndEventId(Long ownerId, Long eventId);

    ResultRequestStatusDto approveRequestByOwnerId(Long ownerId, Long eventId,
                                                        EventRequestStatus eventRequestStatus);
}
