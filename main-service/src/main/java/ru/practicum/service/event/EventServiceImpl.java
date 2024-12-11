package ru.practicum.service.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.dao.*;
import ru.practicum.dto.event.*;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.dto.request.ResultRequestStatusDto;
import ru.practicum.dto.request.Status;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.exceptions.ValidationException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.LocationMapper;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class EventServiceImpl implements EventService {
    EventRepository eventRepository;
    UserRepository userRepository;
    RequestRepository requestRepository;
    CategoryRepository categoryRepository;
    LocationRepository locationRepository;

    @Override
    public EventDto createEvent(Long id, NewEventDto newEventDto) {
        Event event;
        Category category = getCategoryById(newEventDto.getCategory());
        User user = getUserById(id);
        Location location = locationRepository.save(LocationMapper.toLocation(newEventDto.getLocation()));
        checkDateTime(newEventDto.getEventDate());
        event = EventMapper.toEvent(user, category, newEventDto, location);
        event.setState(State.PENDING);
        try {
            event = eventRepository.save(event);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Категория не может быть пустой!");
        }
        return EventMapper.toEventDto(event);
    }

    @Override
    public EventDto updateEvent(Long id, Long eventId, UpdateEventDto updateEventDto) {
        checkExistUser(id);
        Event event = getEventById(eventId);
        Category newCategory = updateEventDto.getCategory() != null ? getCategoryById(updateEventDto.getCategory())
                : event.getCategory();
        if (event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("События можно изменять в статусах: PENDING или CANCELED");
        }

        if (!event.getInitiator().getId().equals(id)) {
            throw new ConflictException("Обновлять информация имеет право только организатор!");
        }

        if (updateEventDto.getEventDate() != null) {
            checkDateTime(updateEventDto.getEventDate());
        }
        return EventMapper.toEventDto(eventRepository.save(EventMapper.toUpdatedEvent(updateEventDto, newCategory,
                event)));
    }

    @Override
    public EventDto getEventByUserIdAndEventId(Long userId, Long eventId) {
        checkExistUser(userId);
        Event event = getEventById(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Информацию о событии может смотреть только организатор!");
        }
        return EventMapper.toEventDto(event);
    }

    @Override
    public List<EventShortDto> getEventsByUserId(Long userId, Integer from, Integer size) {
        checkExistUser(userId);
        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findByInitiatorId(userId, pageable);
        return events.stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestDto> getRequestsByOwnerEventAndEventId(Long ownerId, Long eventId) {
        checkExistUser(ownerId);
        Event event = getEventById(eventId);

        if (!event.getInitiator().getId().equals(ownerId)) {
            throw new ValidationException("Информацию о запросах на участие может смотреть только организатор события!");
        }

        List<Request> requests = requestRepository.findRequestsByEventId(eventId, Sort.by(Sort.Direction.DESC,
                "createdOn"));
        return requests.stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ResultRequestStatusDto approveRequestByOwnerId(Long ownerId, Long eventId, EventRequestStatus eventRequestStatus) {
        checkExistUser(ownerId);
        Event event = getEventById(eventId);

        if (!event.getInitiator().getId().equals(ownerId)) {
            throw new ConflictException("Изменять статус запросов на участие может только организатор события!");
        }

        List<Request> requests = requestRepository.findAllById(eventRequestStatus.getRequestIds());
        for (Request request : requests) {
            if (!request.getStatus().equals(Status.PENDING)) {
                throw new ConflictException("Статус можно изменить только у заявок, находящихся в состоянии ожидания!");
            }
        }

        List<Request> confirmedRequests = new ArrayList<>();
        List<Request> rejectedRequests = new ArrayList<>();

        for (Request request : requests) {
            if (!request.getEvent().getId().equals(eventId)) {
                throw new ConflictException("Запрос с id = " + request.getId() + " не относится к событию с id = " + eventId);
            }

            if (!event.getParticipantLimit().equals(0) && event.getConfirmedRequests().equals(event.getParticipantLimit())) {
                throw new ConflictException("Достигнут лимит запросов!");
            }

            if (event.getParticipantLimit().equals(0) || !event.getRequestModeration()) {
                request.setStatus(Status.CONFIRMED);
                event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                confirmedRequests.add(request);
                continue;
            }

            if (eventRequestStatus.getStatus().equals("CONFIRMED")) {
                if (event.getConfirmedRequests() < event.getParticipantLimit()) {
                    request.setStatus(Status.CONFIRMED);
                    event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                    confirmedRequests.add(request);
                } else {
                    request.setStatus(Status.REJECTED);
                    rejectedRequests.add(request);
                }
            } else if (eventRequestStatus.getStatus().equals("REJECTED")) {
                request.setStatus(Status.REJECTED);
                rejectedRequests.add(request);
            } else {
                throw new ConflictException("Неподдерживаемый статус: " + eventRequestStatus.getStatus());
            }
        }

        if (event.getConfirmedRequests().equals(event.getParticipantLimit())) {
            for (Request request : requests) {
                if (request.getStatus().equals(Status.PENDING)) {
                    request.setStatus(Status.REJECTED);
                    rejectedRequests.add(request);
                }
            }
        }

        requestRepository.saveAll(requests);
        eventRepository.save(event);

        return ResultRequestStatusDto.builder()
                .confirmedRequests(confirmedRequests.stream()
                        .map(RequestMapper::toRequestDto)
                        .collect(Collectors.toList()))
                .rejectedRequests(rejectedRequests.stream()
                        .map(RequestMapper::toRequestDto)
                        .collect(Collectors.toList()))
                .build();
    }

    private User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не был найден!"));
    }

    private Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Категория с id = " + id + " не была найдена!"));
    }

    private Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Событие с id = " + id + " не было найдено!"));
    }

    private void checkExistUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Пользователь с id = " + id + " не был найден!");
        }
    }

    private void checkDateTime(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Дата события должна быть не раньше, чем через два часа от текущего времени.");
        }
    }
}
