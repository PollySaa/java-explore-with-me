package ru.practicum.controller.adminapi;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.service.comment.AdminCommentService;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/admin/comments")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminCommentController {
    AdminCommentService adminCommentService;

    @PatchMapping("/{comment-id}")
    public CommentDto updateStatusComment(@PathVariable("comment-id") Long id) {
        log.info("Выполнение updateStatusComment");
        return adminCommentService.updateStatusComment(id);
    }
}
