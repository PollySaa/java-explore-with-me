package ru.practicum.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.constants.Constants;
import ru.practicum.exceptions.ValidationException;
import ru.practicum.mapper.StatsMapper;
import ru.practicum.model.EndpointHit;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class StatsServiceImpl implements StatsService {
    StatsRepository statsRepository;

    @Override
    public EndpointHit createHit(EndpointHitDto endpointHitDto) {
        return statsRepository.save(StatsMapper.toEndpointHit(endpointHitDto));
    }

    @Override
    public List<ViewStatsDto> getStatsByDateAndUris(String start, String end, List<String> uris, Boolean unique) {
        if (start == null || end == null) {
            throw new ValidationException("Время начала и конца обязательные параметры");
        }
        LocalDateTime startTime;
        LocalDateTime endTime;

        try {
            startTime = LocalDateTime.parse(URLDecoder.decode(start, StandardCharsets.UTF_8), Constants.DATE_PATTERN);
            endTime = LocalDateTime.parse(URLDecoder.decode(end, StandardCharsets.UTF_8), Constants.DATE_PATTERN);
        } catch (DateTimeParseException | IllegalArgumentException e) {
            throw new ValidationException("Период времени нужно передавать в формате 'yyyy-MM-dd HH:mm:ss'");
        }

        if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
            throw new ValidationException("Некорректный временной интервал!");
        }

        List<ViewStatsDto> viewStats;

        if (uris != null && !uris.isEmpty()) {
            if (unique) {
                viewStats = statsRepository.findUniqueIpHitsWithUris(startTime, endTime, uris);
            } else {
                viewStats = statsRepository.findHitsWithUris(startTime, endTime, uris);
            }
        } else {
            if (unique) {
                viewStats = statsRepository.findUniqueIpHits(startTime, endTime);
            } else {
                viewStats = statsRepository.findHits(startTime, endTime);
            }
        }

        return viewStats;
    }
}
