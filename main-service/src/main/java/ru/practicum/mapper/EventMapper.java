package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.event.*;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@UtilityClass
public class EventMapper {

    public static EventDto toEventDto(Event event) {
        return EventDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .createdDate(event.getCreatedDate())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .location(LocationMapper.toLocationDto(event.getLocation()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedDate(event.getPublishedDate())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .views(event.getViews())
                .build();
    }

    public static EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .title(event.getTitle())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .views(event.getViews())
                .build();
    }

    public static Event toEvent(User initiator, Category category, NewEventDto newEventDto) {
        return Event.builder()
                .annotation(newEventDto.getAnnotation())
                .category(category)
                .description(newEventDto.getDescription())
                .createdDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .eventDate(newEventDto.getEventDate())
                .location(LocationMapper.toLocation(newEventDto.getLocation()))
                .paid(newEventDto.getPaid() == null ? Boolean.FALSE : newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit() == null ? 0 : newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.getRequestModeration() == null ? Boolean.TRUE : newEventDto.getRequestModeration())
                .title(newEventDto.getTitle())
                .confirmedRequests(0)
                .initiator(initiator)
                .views(0L)
                .build();
    }

    public static Event toUpdatedEvent(UpdateEventDto updateRequest, Category newCategory, Event oldEvent) {
        State newState = oldEvent.getState();
        if (updateRequest.getStateAction() != null) {
            switch (updateRequest.getStateAction()) {
                case SEND_TO_REVIEW -> newState = State.PENDING;
                case REJECT_EVENT, CANCEL_REVIEW -> newState = State.CANCELED;
                case PUBLISH_EVENT -> {
                    newState = State.PUBLISHED;
                    oldEvent.setPublishedDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
                }
            }
        }
        oldEvent.setTitle(updateRequest.getTitle() == null ? oldEvent.getTitle() : updateRequest.getTitle());
        oldEvent.setAnnotation(updateRequest.getAnnotation() == null ? oldEvent.getAnnotation() : updateRequest.getAnnotation());
        oldEvent.setCategory(newCategory);
        oldEvent.setDescription(updateRequest.getDescription() == null ? oldEvent.getDescription() : updateRequest.getDescription());
        oldEvent.setEventDate(updateRequest.getEventDate() == null ? oldEvent.getEventDate()
                : updateRequest.getEventDate());
        oldEvent.setLocation(updateRequest.getLocation() == null ? oldEvent.getLocation()
                : LocationMapper.toLocation(updateRequest.getLocation()));
        oldEvent.setPaid(updateRequest.getPaid() == null ? oldEvent.getPaid() : updateRequest.getPaid());
        oldEvent.setParticipantLimit(updateRequest.getParticipantLimit() == null ? oldEvent.getParticipantLimit()
                : updateRequest.getParticipantLimit());
        oldEvent.setRequestModeration(updateRequest.getRequestModeration() == null ? oldEvent.getRequestModeration()
                : updateRequest.getRequestModeration());
        oldEvent.setState(newState);
        return oldEvent;
    }
}
