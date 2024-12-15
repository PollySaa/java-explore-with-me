package ru.practicum.controller.adminapi;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventDto;
import ru.practicum.dto.event.State;
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
    public List<EventDto> getEventsWithParam(@RequestParam(required = false) List<Long> users,
                                             @RequestParam(required = false) List<State> states,
                                             @RequestParam(required = false) List<Long> categories,
                                             @RequestParam(required = false) String rangeStart,
                                             @RequestParam(required = false) String rangeEnd,
                                             @RequestParam(defaultValue = "0") Integer from,
                                             @RequestParam(defaultValue = "10") Integer size) {
        log.info("Выполнение getEventsWithParam");
        return adminEventService.getEventsWithParam(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{event-id}")
    public EventDto updateEventByAdmin(@PathVariable("event-id") Long eventId,
                                       @RequestBody @Valid UpdateEventDto updateEventDto) {
        log.info("Выполнение updateEventByAdmin");
        return adminEventService.updateEventByAdmin(eventId, updateEventDto);
    }
}
