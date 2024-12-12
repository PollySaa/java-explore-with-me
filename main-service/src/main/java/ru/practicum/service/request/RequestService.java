package ru.practicum.service.request;

import ru.practicum.dto.request.RequestDto;
import ru.practicum.model.Request;

import java.util.List;

public interface RequestService {

    RequestDto createRequest(Long id, Long eventId);

    RequestDto cancelRequest(Long id, Long requestId);

    List<Request> getRequestsByUserId(Long id);
}
