package ru.practicume;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.EndpointHitDto;
import ru.practicum.stats.model.EndpointHit;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class StatController {
    private final StatClient statsClient;

    @PostMapping("/hit")
    public ResponseEntity<EndpointHit> createHit(@RequestBody EndpointHitDto requestDto) {
        return statsClient.createHit(requestDto);
    }

    @GetMapping("/stats")
    public ResponseEntity<Object> getStatsByDateAndUris(@RequestParam String start,
                                           @RequestParam String end,
                                           @RequestParam(required = false) List<String> uris,
                                           @RequestParam(defaultValue = "false") Boolean unique) {
        return statsClient.getStatsByDateAndUris(start, end, uris, unique);
    }
}
