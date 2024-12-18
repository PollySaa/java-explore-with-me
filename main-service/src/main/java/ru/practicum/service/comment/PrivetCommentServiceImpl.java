package ru.practicum.service.comment;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.dao.CommentRepository;
import ru.practicum.dao.EventRepository;
import ru.practicum.dao.UserRepository;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.event.State;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.IncorrectParameterException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class PrivetCommentServiceImpl implements PrivetCommentService {
    CommentRepository commentRepository;
    UserRepository userRepository;
    EventRepository eventRepository;

    @Override
    public CommentDto createComment(Long authorId, Long eventId, String text) {
        User user = findByUser(authorId);
        Event event = findByEvent(eventId);
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Нельзя оставить комментарий на неопубликованном событии.");
        }
        Comment comment = Comment.builder()
                .author(user)
                .event(event)
                .created(LocalDateTime.now())
                .text(text)
                .status(State.PENDING)
                .build();

        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto updateComment(Long id, Long authorId, String text) {
        Comment comment = findByComment(id);
        if (!comment.getAuthor().getId().equals(authorId)) {
            throw new IncorrectParameterException("Не верный id автора комметария!");
        }

        if (comment.getText().equals(text)) {
            throw new ConflictException("Ваш комментарий не изменился!");
        }

        if (LocalDateTime.now().isAfter(comment.getCreated().plusHours(48))) {
            throw new ConflictException("Изменять комментарий можно в течение двух суток!");
        }

        comment.setText(text);
        comment.setChanged(LocalDateTime.now());
        comment.setStatus(State.PENDING);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public void deleteComment(Long id, Long authorId) {
        Comment comment = findByComment(id);
        if (!comment.getAuthor().getId().equals(authorId)) {
            throw new IncorrectParameterException("Не верный id автора комметария!");
        }
        if (LocalDateTime.now().isAfter(comment.getCreated().plusHours(48))) {
            throw new ConflictException("Удалить комментарий можно в течение двух суток!");
        }
        commentRepository.deleteById(id);
    }

    @Override
    public List<CommentDto> getComments(Long authorId, Long eventId, Boolean authorSort, Boolean createdSort) {
        findByUser(authorId);
        findByEvent(eventId);
        if (authorSort == null || createdSort == null) {
            return commentRepository.findByEventId(eventId).stream()
                    .map(CommentMapper::toCommentDto)
                    .collect(Collectors.toList());
        }

        if (authorSort && createdSort) {
            return commentRepository.findByEventIdOrderByCreatedDescAuthorNameDesc(eventId).stream()
                    .map(CommentMapper::toCommentDto)
                    .collect(Collectors.toList());
        }

        if (authorSort) {
            return commentRepository.findByEventIdOrderByAuthorNameDesc(eventId).stream()
                    .map(CommentMapper::toCommentDto)
                    .collect(Collectors.toList());
        }

        if (createdSort) {
            return commentRepository.findByEventIdOrderByCreatedDesc(eventId).stream()
                    .map(CommentMapper::toCommentDto)
                    .collect(Collectors.toList());
        }
        throw new IncorrectParameterException("Что-то не так!");
    }

    private User findByUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не был найден!"));
    }

    private Comment findByComment(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Комментарий с id = " + id + " не был найден!"));
    }

    private Event findByEvent(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Событие с id = " + id + " не было найдено!"));
    }
}
