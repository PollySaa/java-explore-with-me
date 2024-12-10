package ru.practicum.service.request;

import ru.practicum.dto.request.RequestDto;
import ru.practicum.dto.request.ResultRequestStatusDto;

import java.util.List;

public interface RequestService {

    RequestDto createRequest(Long id, Long eventId);

    RequestDto cancelRequest(Long id, Long requestId);

    List<ResultRequestStatusDto> getRequestsByUserId(Long id);
}
