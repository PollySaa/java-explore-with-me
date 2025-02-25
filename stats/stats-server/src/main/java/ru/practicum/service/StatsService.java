package ru.practicum.service;

import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.model.EndpointHit;

import java.util.List;

public interface StatsService {
    EndpointHit createHit(EndpointHitDto endpointHitDto);

    List<ViewStatsDto> getStatsByDateAndUris(String start, String end, List<String> uris, Boolean unique);
}
