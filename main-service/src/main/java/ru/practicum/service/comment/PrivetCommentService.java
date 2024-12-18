package ru.practicum.service.comment;

import ru.practicum.dto.comment.CommentDto;

import java.util.List;

public interface PrivetCommentService {

    CommentDto createComment(Long authorId, Long eventId, String text);

    CommentDto updateComment(Long id, Long authorId, String text);

    void deleteComment(Long id, Long authorId);

    List<CommentDto> getComments(Long authorId, Long eventId, Boolean authorSort, Boolean createdSort);
}

