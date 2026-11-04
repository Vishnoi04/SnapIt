package com.snapit.service;

import com.snapit.dto.request.CollectionRequest;
import com.snapit.dto.response.CollectionResponse;
import com.snapit.dto.response.SavedItemsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CollectionService {
    CollectionResponse create(CollectionRequest request);

    Page<CollectionResponse> getAll(Pageable pageable);

    CollectionResponse update(Long collectionId, CollectionRequest request);

    void delete(Long collectionId);

    void addItem(Long collectionId, Long savedItemId);

    void removeItem(Long collectionId, Long savedItemId);

    Page<SavedItemsResponse> getItems(Long collectionId, Pageable pageable);
}
