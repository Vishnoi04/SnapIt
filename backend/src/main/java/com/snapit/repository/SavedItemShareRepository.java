package com.snapit.repository;

import com.snapit.entity.SavedItemShare;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SavedItemShareRepository extends JpaRepository<SavedItemShare, Long> {
    boolean existsBySavedItemIdAndRecipientId(Long savedItemId, Long recipientId);

    @EntityGraph(attributePaths = {"savedItem", "recipient"})
    Page<SavedItemShare> findAllBySavedItemUserId(Long ownerId, Pageable pageable);

    @EntityGraph(attributePaths = {"savedItem", "recipient"})
    Page<SavedItemShare> findAllByRecipientId(Long recipientId, Pageable pageable);

    @EntityGraph(attributePaths = "savedItem")
    Optional<SavedItemShare> findBySavedItemIdAndRecipientId(Long savedItemId, Long recipientId);

    Optional<SavedItemShare> findByIdAndSavedItemUserId(Long id, Long ownerId);
}
