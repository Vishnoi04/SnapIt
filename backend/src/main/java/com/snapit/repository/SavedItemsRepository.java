package com.snapit.repository;

import com.snapit.entity.SavedItems;
import com.snapit.enums.MemoryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SavedItemsRepository extends JpaRepository<SavedItems, Long> {
    @Query("""
        SELECT s FROM SavedItems s
        WHERE s.user.id = :userId
        AND (
            LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(s.extractedText) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
        AND (:contentType IS NULL OR s.contentType = :contentType)
        """)
    Page<SavedItems> searchItems(@Param("userId") Long userId, @Param("keyword") String keyword, @Param("contentType") MemoryType contentType, Pageable pageable);

    List<SavedItems> findAllByUserId(Long userId);

    Page<SavedItems> findAllByUserId(Long userId, Pageable pageable);

    Optional<SavedItems> findByIdAndUserId(Long id, Long userId);
}
