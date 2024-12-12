package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.dto.request.ResultRequestStatusDto;
import ru.practicum.dto.request.Status;
import ru.practicum.model.Event;
import ru.practicum.model.Request;
import ru.practicum.model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@UtilityClass
public class RequestMapper {

    public static RequestDto toRequestDto(Request request) {
        return RequestDto.builder()
                .id(request.getId())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .created(request.getCreated())
                .status(request.getStatus())
                .build();
    }

    public static Request toRequest(Event event, User requester) {
        return Request.builder()
                .event(event)
                .requester(requester)
                .created(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .status(event.getRequestModeration() ? Status.PENDING : Status.CONFIRMED)
                .build();
    }

    public static ResultRequestStatusDto toResultRequest(List<Request> requests) {
        return ResultRequestStatusDto.builder()
                .confirmedRequests(requests.stream()
                        .filter(request -> request.getStatus().equals(Status.CONFIRMED))
                        .map(RequestMapper::toRequestDto)
                        .toList())
                .rejectedRequests(requests.stream()
                        .filter(request -> request.getStatus().equals(Status.REJECTED))
                        .map(RequestMapper::toRequestDto)
                        .toList())
                .build();
    }
}
