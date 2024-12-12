package ru.practicum.controller.publicapi;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventDto;
import ru.practicum.dto.event.EventPublic;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.service.event.PublicEventService;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/events")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PublicEventController {
    PublicEventService publicEventService;

    @GetMapping
    public List<EventShortDto> getEvents(EventPublic eventPublic, HttpServletRequest request) {
        log.info("Выполнение getEvents size={}", eventPublic.getSize());
        return publicEventService.getEvents(eventPublic, request);
    }

    @GetMapping("/{event-id}")
    public EventDto getEventByIdByPublicUser(@PathVariable("event-id") Long id,
                                             HttpServletRequest request) {
        log.info("Выполнение getEventByIdByPublicUser");
        return publicEventService.getEventByIdByPublicUser(id, request);
    }
}
