package com.snapit.service;

import com.snapit.dto.request.SavedItemsRequest;
import com.snapit.dto.response.SavedItemsResponse;
import com.snapit.entity.SavedItems;
import com.snapit.enums.MemoryType;
import com.snapit.enums.ProcessingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SavedItemsService {
    SavedItemsResponse saveItem(SavedItemsRequest saveItem);
    List<SavedItemsResponse> getAllSavedItems();
    SavedItemsResponse getSavedItemById(Long Id);
    void deleteSavedItemById(Long Id);
    void updateProcessingStatus(Long Id, ProcessingStatus processingStatus);
    Page<SavedItemsResponse> searchItems(String keyword, MemoryType contentType, Pageable pageable);
}
