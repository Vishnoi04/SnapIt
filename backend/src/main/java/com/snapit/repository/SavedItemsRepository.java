package com.snapit.repository;

import com.snapit.entity.SavedItems;
import com.snapit.enums.MemoryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SavedItemsRepository extends JpaRepository<SavedItems, Long> {
    @Query(
            """
            SELECT s FROM SavedItems s
            WHERE (
                LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(s.extractedText) LIKE LOWER(CONCAT('%', :keyword, '%'))
            )
            AND (:contentType IS NULL OR s.contentType = :contentType)
            """
    )
    Page<SavedItems> searchItems(@Param("keyword") String keyword, @Param("contentType") MemoryType contentType, Pageable pageable);
}
