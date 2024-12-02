package ru.practicume;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.stats.model.EndpointHit;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class StatsClient {
    private final RestTemplate rest;
    private final String urlStats;

    public StatsClient(RestTemplate rest, @Value("${gateway.url}") String urlStats) {
        this.rest = rest;
        this.urlStats = urlStats;
    }

    public EndpointHit createHit(EndpointHitDto endpointHitDto) {
        HttpEntity<EndpointHitDto> requestEntity = new HttpEntity<>(endpointHitDto);
        ResponseEntity<EndpointHit> responseEntity = rest.postForEntity(urlStats + "/hit", requestEntity,
                EndpointHit.class);
        return responseEntity.getBody();
    }

    public ViewStatsDto getStatsByDateAndUris(String start, String end, List<String> uris, Boolean unique) {

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(urlStats + "/stats")
                .queryParam("start", start).encode(StandardCharsets.UTF_8)
                .queryParam("end", end).encode(StandardCharsets.UTF_8)
                .queryParam("uris", uris)
                .queryParam("unique", unique);
        return rest.getForObject(builder.toUriString(), ViewStatsDto.class);
    }
}
