package ru.practicum.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.dto.event.State;
import ru.practicum.model.Category;
import ru.practicum.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByInitiatorId(Long userId, Pageable pageable);

    @Query("""
            select e
            from Event as e
            where (?1 is null or (e.annotation ilike %?1% or e.description ilike %?1%))
            and (?2 is null or e.category.id in ?2)
            and (?3 is null or e.paid = ?3)
            and e.eventDate between ?4 and ?5
            and (?6 = false or e.confirmedRequests < e.participantLimit)
            and e.state = 'PUBLISHED'
            """)
    List<Event> findAllPublishedEventsByFilterAndPeriod(String text, List<Long> categories, Boolean paid,
                                                        LocalDateTime start, LocalDateTime end, Boolean onlyAvailable,
                                                        Pageable pageable);

    @Query("""
            select e
            from Event as e
            where (?1 is null or (e.annotation ilike %?1% or e.description ilike %?1%))
            and (?2 is null or e.category.id in ?2)
            and (?3 is null or e.paid = ?3)
            and e.eventDate >= ?4
            and (?5 = false or e.confirmedRequests < e.participantLimit)
            and e.state = 'PUBLISHED'
            """)
    List<Event> findAllPublishedEventsByFilterAndStart(String text, List<Long> categories, Boolean paid,
                                                            LocalDateTime start, Boolean onlyAvailable,
                                                            Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "WHERE (:users IS NULL OR e.initiator.id IN :users) " +
            "AND (:states IS NULL OR e.state IN :states) " +
            "AND (:categories IS NULL OR e.category.id IN :categories) " +
            "AND (:start IS NULL OR e.eventDate >= :start) " +
            "AND (:end IS NULL OR e.eventDate <= :end)")
    List<Event> findAllByParams(
            @Param("users") List<Long> users,
            @Param("states") List<State> states,
            @Param("categories") List<Long> categories,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable);

    @Query("SELECT e FROM Event e")
    List<Event> findAllEvents(Pageable pageable);

    boolean existsByCategory(Category category);
}
