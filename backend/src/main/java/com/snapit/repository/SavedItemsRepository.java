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
            AND (:keyword IS NULL OR :keyword = '' OR
                LOWER(COALESCE(s.title, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(COALESCE(s.description, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(COALESCE(s.extractedText, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(COALESCE(s.sourceUrl, '')) LIKE LOWER(CONCAT('%', :keyword, '%')))
            AND (:contentType IS NULL OR s.contentType = :contentType)
            AND (:favorite IS NULL OR s.favorite = :favorite)
            AND (:tagId IS NULL OR EXISTS (
                SELECT tag.id FROM Tag tag
                JOIN tag.items taggedItem
                WHERE tag.id = :tagId
                  AND tag.user.id = :userId
                  AND taggedItem = s
            ))
            """)
    Page<SavedItems> searchItems(
            @Param("userId") Long userId,
            @Param("keyword") String keyword,
            @Param("contentType") MemoryType contentType,
            @Param("favorite") Boolean favorite,
            @Param("tagId") Long tagId,
            Pageable pageable
    );

    List<SavedItems> findAllByUserId(Long userId);

    Page<SavedItems> findAllByUserId(Long userId, Pageable pageable);

    Page<SavedItems> findAllByUserIdAndFavoriteTrue(Long userId, Pageable pageable);

    @Query("""
            SELECT s.sourceUrl FROM SavedItems s
            WHERE s.user.id = :userId
              AND s.contentType = :contentType
              AND s.sourceUrl IS NOT NULL
            """)
    List<String> findSourceUrlsByUserIdAndContentType(
            @Param("userId") Long userId,
            @Param("contentType") MemoryType contentType
    );

    Optional<SavedItems> findByIdAndUserId(Long id, Long userId);
}
