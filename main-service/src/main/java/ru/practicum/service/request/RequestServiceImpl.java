package ru.practicum.service.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.dao.EventRepository;
import ru.practicum.dao.RequestRepository;
import ru.practicum.dao.UserRepository;
import ru.practicum.dto.event.State;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.dto.request.ResultRequestStatusDto;
import ru.practicum.dto.request.Status;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.IncorrectParameterException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.Event;
import ru.practicum.model.Request;
import ru.practicum.model.User;

import java.util.ArrayList;
import java.util.List;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {
    RequestRepository requestRepository;
    EventRepository eventRepository;
    UserRepository userRepository;

    @Override
    public RequestDto createRequest(Long id, Long eventId) {
        if (eventId == null) {
            throw new IncorrectParameterException("eventId не может быть null");
        }

        if (requestRepository.existsByRequesterIdAndEventId(id, eventId)) {
            throw new ConflictException("Нельзя создавать одинаковый запрос на уже существующее событие!");
        }

        User user = getUserById(id);
        Event event = getEventById(eventId);
        if (id.equals(event.getInitiator().getId())) {
            throw new ConflictException("Инициатор события не может добавить запрос на участие в своём же событии");
        }

        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Событие должно быть опубликованно!");
        }

        if (event.getParticipantLimit() != 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ConflictException("Достигнут лимит запросов!");
        }

        Request request = RequestMapper.toRequest(event, user);
        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            request.setStatus(Status.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        }
        return RequestMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    public RequestDto cancelRequest(Long id, Long requestId) {
        getUserById(id);
        Request request = getRequestById(requestId);
        if (request.getStatus().equals(Status.CANCELED)) {
            throw new IncorrectParameterException("Событие уже отменено.");
        }

        if (!id.equals(request.getRequester().getId())) {
            throw new IncorrectParameterException("Событие может отменять только создатель запроса.");
        }
        request.setStatus(Status.CANCELED);
        return RequestMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    public List<ResultRequestStatusDto> getRequestsByUserId(Long id) {
        getUserById(id);
        List<Request> requests = requestRepository.findAllByRequesterId(id, Sort.by(Sort.Direction.DESC,
                "created"));
        return new ArrayList<>(List.of(RequestMapper.toRequest(requests)));
    }

    private User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не был найден!"));
    }

    private Request getRequestById(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Запрос с id = " + id + " не был найден!"));
    }

    private Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Событие с id = " + id + " не было найдено!"));
    }
}
