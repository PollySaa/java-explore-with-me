package ru.practicum.controller.privetapi;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.*;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.dto.request.ResultRequestStatusDto;
import ru.practicum.service.event.EventService;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/users")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventController {
    EventService eventService;

    @PostMapping("/{user-id}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto createEvent(@PathVariable("user-id") Long id,
                                @RequestBody NewEventDto newEventDto) {
        log.info("Выполнение createEvent");
        return eventService.createEvent(id, newEventDto);
    }

    @PatchMapping("/{user-id}/events/{event-id}")
    public EventDto updateEvent(@PathVariable("user-id") Long id,
                                @PathVariable("event-id") Long eventId,
                                @RequestBody UpdateEventDto updateEventDto) {
        log.info("Выполнение updateEvent");
        return eventService.updateEvent(id, eventId, updateEventDto);
    }

    @GetMapping("/{user-id}/events/{event-id}")
    public EventDto getEventByUserIdAndEventId(@PathVariable("user-id") Long id,
                                               @PathVariable("event-id") Long eventId) {
        log.info("Выполнение getEventByUserIdAndEventId");
        return eventService.getEventByUserIdAndEventId(id, eventId);
    }

    @GetMapping("/{user-id}/events")
    public List<EventShortDto> getEventsByUserId(@PathVariable("user-id") Long userId,
                                                 @RequestParam Integer from,
                                                 @RequestParam Integer size) {
        log.info("Выполнение getEventsByUserId");
        return eventService.getEventsByUserId(userId, from, size);
    }

    @GetMapping("/{user-id}/events/{event-id}/requests")
    public List<RequestDto> getRequestsByOriginatorEventAndEventId(@PathVariable("user-id") Long ownerId,
                                                                   @PathVariable("event-id") Long eventId) {
        log.info("Выполнение getRequestsByOriginatorEventAndEventId");
        return eventService.getRequestsByOwnerEventAndEventId(ownerId, eventId);
    }

    @PatchMapping("/{user-id}/events/{event-id}/requests")
    public ResultRequestStatusDto approveRequestByOriginatorId(@PathVariable("user-id") Long ownerId,
                                                               @PathVariable("event-id") Long eventId,
                                                               @RequestBody EventRequestStatus eventRequestStatus) {
        log.info("Выполнение approveRequestByOriginatorId");
        return eventService.approveRequestByOwnerId(ownerId, eventId, eventRequestStatus);
    }
}
