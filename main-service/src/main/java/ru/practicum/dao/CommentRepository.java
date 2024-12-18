package ru.practicum.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.event.id = ?1")
    List<Comment> findByEventId(Long id);

    @Query("SELECT c FROM Comment c WHERE c.event.id = ?1 ORDER BY c.created DESC, c.author.name DESC")
    List<Comment> findByEventIdOrderByCreatedDescAuthorNameDesc(Long id);

    @Query("SELECT c FROM Comment c WHERE c.event.id = ?1 ORDER BY c.author.name DESC")
    List<Comment> findByEventIdOrderByAuthorNameDesc(Long id);

    @Query("SELECT c FROM Comment c WHERE c.event.id = ?1 ORDER BY c.created DESC")
    List<Comment> findByEventIdOrderByCreatedDesc(Long id);
}
