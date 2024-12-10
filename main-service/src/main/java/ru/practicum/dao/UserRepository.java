package ru.practicum.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("""
            select u
            from User as u
            where (?1 is null or u.id in ?1)
            """)
    List<User> findUserByIds(List<Long> ids, Pageable pageable);
}
