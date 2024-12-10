package ru.practicum.controller.adminapi;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventAdmin;
import ru.practicum.dto.event.EventDto;
import ru.practicum.dto.event.UpdateEventDto;
import ru.practicum.service.event.AdminEventService;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/admin/events")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminEventController {
    AdminEventService adminEventService;

    @GetMapping
    public List<EventDto> getEventsWithParam(@RequestBody EventAdmin eventAdmin) {
        log.info("Выполнение getEventsWithParam");
        return adminEventService.getEventsWithParam(eventAdmin);
    }

    @PatchMapping("/{event-id}")
    public EventDto updateEventByAdmin(@PathVariable("event-id") Long eventId,
                                       @RequestBody @Valid UpdateEventDto updateEventDto) {
        log.info("Выполнение updateEventByAdmin");
        return adminEventService.updateEventByAdmin(eventId, updateEventDto);
    }
}
