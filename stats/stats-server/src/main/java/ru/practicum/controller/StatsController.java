package ru.practicum.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.model.EndpointHit;
import ru.practicum.service.StatsService;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatsController {
    StatsService service;

    @PostMapping("/hit")
    public EndpointHit createHit(@RequestBody EndpointHitDto requestDto) {
        log.info("Выполнение createHit");
        return service.createHit(requestDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStatsByDateAndUris(@RequestParam String start,
                                                    @RequestParam String end,
                                                    @RequestParam(required = false) List<String> uris,
                                                    @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("Выполнение getStatsByDateAndUris");
        return service.getStatsByDateAndUris(start, end, uris, unique);
    }
}
