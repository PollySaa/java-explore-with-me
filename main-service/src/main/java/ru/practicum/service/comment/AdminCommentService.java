package ru.practicum.service.comment;

import ru.practicum.dto.comment.CommentDto;

public interface AdminCommentService {

    CommentDto updateStatusComment(Long id);
}
