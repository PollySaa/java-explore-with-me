package ru.practicume.stats;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class StatsClient  {
    private final RestTemplate rest;
    private final String statUrl;

    public StatsClient(RestTemplate rest, @Value("${stats-gateway.url}") String statUrl) {
        this.rest = rest;
        this.statUrl = statUrl;
    }

    public void createHitStats(EndpointHitDto statRequestDto) {
        HttpEntity<EndpointHitDto> requestEntity = new HttpEntity<>(statRequestDto);
        rest.postForEntity(statUrl + "/hit", requestEntity, Void.class);
    }

    public ViewStatsDto getStatsByDateAndUris(String start, String end, List<String> uris, Boolean unique) {

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(statUrl + "/stats")
                .queryParam("start", start).encode(StandardCharsets.UTF_8)
                .queryParam("end", end).encode(StandardCharsets.UTF_8)
                .queryParam("uris", uris)
                .queryParam("unique", unique);
        return rest.getForObject(builder.toUriString(), ViewStatsDto.class);
    }
}
