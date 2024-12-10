package ru.practicum.stats;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class StatsClient  {
    private final RestTemplate rest = new RestTemplate();
    @Value("${stats-gateway.url}")
    private String statsUrl;

    public void createHit(EndpointHitDto statRequestDto) {
        HttpEntity<EndpointHitDto> requestEntity = new HttpEntity<>(statRequestDto);
        rest.postForEntity(statsUrl + "/hit", requestEntity, Void.class);
    }

    public List<ViewStatsDto> getStatsByDateAndUris(String start, String end, List<String> uris, Boolean unique) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(statsUrl + "/stats")
                .queryParam("start", start).encode(StandardCharsets.UTF_8)
                .queryParam("end", end).encode(StandardCharsets.UTF_8)
                .queryParam("uris", uris)
                .queryParam("unique", unique);
        ResponseEntity<List<ViewStatsDto>> responseEntity = rest.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ViewStatsDto>>() {});

        return responseEntity.getBody();
    }
}
