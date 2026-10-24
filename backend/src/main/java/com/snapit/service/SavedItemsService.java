package com.snapit.service;

import com.snapit.entity.SavedItems;

import java.util.List;
import java.util.Optional;

public interface SavedItemsService {
    SavedItems saveItem(SavedItems saveItem);
    List<SavedItems> getAllSavedItems();
    Optional<SavedItems> getSavedItemById(Long Id);
    void deleteSavedItemById(Long Id);
}
