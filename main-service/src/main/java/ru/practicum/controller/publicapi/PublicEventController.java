package ru.practicum.controller.publicapi;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventDto;
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
    public List<EventShortDto> getEvents(@RequestParam(required = false) String text,
                                         @RequestParam(required = false) List<Long> categories,
                                         @RequestParam(required = false) Boolean paid,
                                         @RequestParam(required = false) String rangeStart,
                                         @RequestParam(required = false) String rangeEnd,
                                         @RequestParam(required = false) Boolean onlyAvailable,
                                         @RequestParam(required = false) String sort,
                                         @RequestParam Integer from,
                                         @RequestParam Integer size,
                                         @RequestHeader(value = "X-Forwarded-For", required = false) String ip,
                                         HttpServletRequest request) {
        log.info("Выполнение getEvents");
        return publicEventService.getEvents(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size, ip, request);
    }

    @GetMapping("/{event-id}")
    public EventDto getEventByIdByPublicUser(@PathVariable("event-id") Long id,
                                             @RequestHeader(value = "X-Forwarded-For", required = false) String ip,
                                             HttpServletRequest request) {
        log.info("Выполнение getEventByIdByPublicUser");
        return publicEventService.getEventByIdByPublicUser(id, ip, request);
    }
}
