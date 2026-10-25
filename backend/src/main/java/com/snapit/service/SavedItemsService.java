package com.snapit.service;

import com.snapit.dto.request.SavedItemsRequest;
import com.snapit.dto.response.SavedItemsResponse;

import java.util.List;

public interface SavedItemsService {
    SavedItemsResponse saveItem(SavedItemsRequest saveItem);
    List<SavedItemsResponse> getAllSavedItems();
    SavedItemsResponse getSavedItemById(Long Id);
    void deleteSavedItemById(Long Id);
}
