package com.snapit.service;

import com.snapit.dto.request.SavedItemsRequest;
import com.snapit.dto.response.SavedItemsResponse;
import com.snapit.enums.MemoryType;
import com.snapit.enums.ProcessingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.core.io.Resource;

import java.util.List;

public interface SavedItemsService {
    SavedItemsResponse saveItem(SavedItemsRequest saveItem);

    List<SavedItemsResponse> getAllSavedItems();

    Page<SavedItemsResponse> getAllSavedItems(Pageable pageable);

    Page<SavedItemsResponse> getFavoriteSavedItems(Pageable pageable);

    SavedItemsResponse getSavedItemById(Long Id);

    void deleteSavedItemById(Long Id);

    void updateProcessingStatus(Long Id, ProcessingStatus processingStatus);

    Page<SavedItemsResponse> searchItems(
            String keyword,
            MemoryType contentType,
            Boolean favorite,
            Long tagId,
            Pageable pageable
    );

    SavedItemsResponse updateSavedItem(Long id, SavedItemsRequest request);

    SavedItemsResponse updateFavorite(Long id, boolean favorite);

    Resource getOwnedFile(Long id);

    void deleteOwnedFile(Long id);
}
