package com.snapit.repository;

import com.snapit.entity.Reminder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    @EntityGraph(attributePaths = "savedItem")
    Page<Reminder> findAllBySavedItemUserId(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = "savedItem")
    Page<Reminder> findAllBySavedItemUserIdAndCompleted(
            Long userId,
            boolean completed,
            Pageable pageable
    );

    @EntityGraph(attributePaths = "savedItem")
    Page<Reminder> findAllBySavedItemUserIdAndCompletedFalseAndRemindAtLessThanEqual(
            Long userId,
            LocalDateTime dueAt,
            Pageable pageable
    );

    @EntityGraph(attributePaths = "savedItem")
    Optional<Reminder> findByIdAndSavedItemUserId(Long id, Long userId);
}
