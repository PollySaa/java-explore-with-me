package ru.practicum.service.comment;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.dao.CommentRepository;
import ru.practicum.dao.RequestRepository;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.event.State;
import ru.practicum.dto.request.Status;
import ru.practicum.exceptions.IncorrectParameterException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.model.Comment;
import ru.practicum.model.Request;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class AdminCommentServiceImpl implements AdminCommentService {
    CommentRepository commentRepository;
    RequestRepository requestRepository;

    @Override
    public CommentDto updateStatusComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Комментарий с id = " + id + " не был найден!"));
        Long eventId = comment.getEvent().getId();
        Long authorId = comment.getAuthor().getId();
        if (comment.getEvent().getRequestModeration().equals(true)) {
            Request request = requestRepository.findByRequesterIdAndEventId(authorId, eventId);
            if (!request.getStatus().equals(Status.CONFIRMED)) {
                comment.setStatus(State.CANCELED);
                commentRepository.save(comment);
                throw new IncorrectParameterException("Комментарий можно оставить только на посещённое событие!");
            }
            if (comment.getStatus().equals(State.PENDING)) {
                comment.setStatus(State.PUBLISHED);
                return CommentMapper.toCommentDto(commentRepository.save(comment));
            }
        }
        if (comment.getStatus().equals(State.PENDING)) {
            comment.setStatus(State.PUBLISHED);
        }
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }
}
