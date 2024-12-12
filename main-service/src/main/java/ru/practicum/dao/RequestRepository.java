package ru.practicum.dao;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    Boolean existsByRequesterIdAndEventId(Long id, Long eventId);

    List<Request> findAllByRequesterId(Long id, Sort sort);

    List<Request> findRequestsByEventId(Long eventId, Sort sort);
}
