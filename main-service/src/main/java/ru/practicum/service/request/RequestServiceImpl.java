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
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.exceptions.ValidationException;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.Event;
import ru.practicum.model.Request;
import ru.practicum.model.User;

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
        User user = getUserById(id);
        Event event = getEventById(eventId);
        if (requestRepository.existsByRequesterIdAndEventId(id, eventId)) {
            throw new ValidationException("Нельзя создавать одинаковый запрос на уже существующее событие!");
        }

        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ValidationException("Событие должно быть опубликованно!");
        }

        if (!event.getParticipantLimit().equals(0) && event.getConfirmedRequests().equals(event.getParticipantLimit())) {
            throw new ValidationException("Достигнут лимит запросов!");
        }

        Request request = RequestMapper.toRequest(event, user);
        if (event.getParticipantLimit().equals(0) || (!event.getParticipantLimit().equals(0) && !event.getRequestModeration())) {
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
            throw new ValidationException("Событие отменено.");
        }

        if (!id.equals(request.getRequester().getId())) {
            throw new ValidationException("Событие может отменять только создаель запроса.");
        }
        request.setStatus(Status.CANCELED);
        return RequestMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    public List<ResultRequestStatusDto> getRequestsByUserId(Long id) {
        getUserById(id);
        List<Request> requests = requestRepository.findAllByRequesterId(id, Sort.by(Sort.Direction.DESC,
                "createdDate"));
        return List.of(RequestMapper.toRequest(requests));
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
