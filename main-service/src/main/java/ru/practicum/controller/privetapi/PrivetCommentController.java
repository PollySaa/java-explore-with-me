package ru.practicum.controller.privetapi;

import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.service.comment.PrivetCommentService;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/users/{user-id}/comments")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PrivetCommentController {
    PrivetCommentService privetCommentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@PathVariable("user-id") Long id,
                                    @RequestParam Long eventId,
                                    @RequestParam @Size(min = 10, max = 1000) String text) {
        log.info("Выполнение createComment");
        return privetCommentService.createComment(id, eventId, text);
    }

    @PatchMapping("/{comment-id}")
    public CommentDto updateComment(@PathVariable("user-id") Long userId,
                                    @PathVariable("comment-id") Long id,
                                    @RequestParam @Size(min = 10, max = 1000) String text) {
        log.info("Выполнение updateComment");
        return privetCommentService.updateComment(id, userId, text);
    }

    @DeleteMapping("/{comment-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable("user-id") Long userId,
                              @PathVariable("comment-id") Long id) {
        log.info("Выполнение deleteComment");
        privetCommentService.deleteComment(id, userId);
    }

    @GetMapping
    public List<CommentDto> getComments(@PathVariable("user-id") Long userId,
                                        @RequestParam Long eventId,
                                        @RequestParam(required = false) Boolean authorSort,
                                        @RequestParam(required = false) Boolean createdSort) {
        log.info("Выполнение getComments");
        return privetCommentService.getComments(userId, eventId, authorSort, createdSort);
    }
}
