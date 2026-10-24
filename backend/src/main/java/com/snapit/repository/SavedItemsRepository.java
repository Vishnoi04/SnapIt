package com.snapit.repository;

import com.snapit.entity.SavedItems;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SavedItemsRepository extends JpaRepository<SavedItems, Long> {
}
