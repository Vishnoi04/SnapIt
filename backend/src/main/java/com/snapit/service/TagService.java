package com.snapit.service;

import com.snapit.dto.request.TagRequest;
import com.snapit.dto.response.SavedItemsResponse;
import com.snapit.dto.response.TagResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TagService {
    TagResponse create(TagRequest request);

    Page<TagResponse> getAll(Pageable pageable);

    TagResponse update(Long tagId, TagRequest request);

    void delete(Long tagId);

    void addItem(Long tagId, Long savedItemId);

    void removeItem(Long tagId, Long savedItemId);

    Page<SavedItemsResponse> getItems(Long tagId, Pageable pageable);
}
