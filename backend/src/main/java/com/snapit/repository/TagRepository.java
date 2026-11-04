package com.snapit.repository;

import com.snapit.entity.SavedItems;
import com.snapit.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    boolean existsByUserIdAndNameIgnoreCase(Long userId, String name);

    boolean existsByUserIdAndNameIgnoreCaseAndIdNot(Long userId, String name, Long id);

    Page<Tag> findAllByUserId(Long userId, Pageable pageable);

    Optional<Tag> findByIdAndUserId(Long id, Long userId);

    @Query("""
            SELECT item FROM Tag tag
            JOIN tag.items item
            WHERE tag.id = :tagId
              AND tag.user.id = :userId
              AND item.user.id = :userId
            """)
    Page<SavedItems> findItemsByTagIdAndUserId(
            @Param("tagId") Long tagId,
            @Param("userId") Long userId,
            Pageable pageable
    );
}
