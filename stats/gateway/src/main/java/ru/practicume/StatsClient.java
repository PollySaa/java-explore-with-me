package ru.practicume;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class StatsClient {
    RestTemplate restTemplate;
    String statsServiceUrl;

    public void createHit(EndpointHitDto requestDto) {
        String url = statsServiceUrl + "/hit";
        HttpEntity<EndpointHitDto> requestEntity = new HttpEntity<>(requestDto);
        restTemplate.exchange(url, HttpMethod.POST, requestEntity, Void.class);
    }

    public List<ViewStatsDto> getStatsByPeriodAndUris(String start, String end, List<String> uris, Boolean unique) {
        try {
            String decodedStart = URLDecoder.decode(start, StandardCharsets.UTF_8);
            String decodedEnd = URLDecoder.decode(end, StandardCharsets.UTF_8);

            String url = UriComponentsBuilder.fromHttpUrl(statsServiceUrl + "/stats")
                    .queryParam("start", decodedStart)
                    .queryParam("end", decodedEnd)
                    .queryParam("uris", uris != null ? String.join(",", uris) : null)
                    .queryParam("unique", unique)
                    .toUriString();

            ResponseEntity<List<ViewStatsDto>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<ViewStatsDto>>() {}
            );

            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
