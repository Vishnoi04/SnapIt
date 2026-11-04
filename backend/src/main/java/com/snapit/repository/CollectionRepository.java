package com.snapit.repository;

import com.snapit.entity.Collection;
import com.snapit.entity.SavedItems;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CollectionRepository extends JpaRepository<Collection, Long> {
    boolean existsByUserIdAndNameIgnoreCase(Long userId, String name);

    boolean existsByUserIdAndNameIgnoreCaseAndIdNot(Long userId, String name, Long id);

    Page<Collection> findAllByUserId(Long userId, Pageable pageable);

    Optional<Collection> findByIdAndUserId(Long id, Long userId);

    @Query("""
            SELECT item FROM Collection collection
            JOIN collection.items item
            WHERE collection.id = :collectionId
              AND collection.user.id = :userId
              AND item.user.id = :userId
            """)
    Page<SavedItems> findItemsByCollectionIdAndUserId(
            @Param("collectionId") Long collectionId,
            @Param("userId") Long userId,
            Pageable pageable
    );
}
