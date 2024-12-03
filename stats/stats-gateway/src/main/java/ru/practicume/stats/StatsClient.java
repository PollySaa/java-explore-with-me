package ru.practicume.stats;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.model.EndpointHit;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class StatsClient  {
    private final RestTemplate rest;
    private final String statUrl;

    public StatsClient(RestTemplate rest, @Value("${client.url}") String statUrl) {
        this.rest = rest;
        this.statUrl = statUrl;
    }

    public ResponseEntity<EndpointHit> createHit(EndpointHitDto endpointHitDto) {
        HttpEntity<EndpointHitDto> requestEntity = new HttpEntity<>(endpointHitDto);
        return rest.postForEntity(statUrl, requestEntity, EndpointHit.class);
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
