package com.snapit.service;

import com.snapit.dto.request.ShareSavedItemRequest;
import com.snapit.dto.response.SavedItemsResponse;
import com.snapit.dto.response.ShareResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ShareService {
    ShareResponse share(ShareSavedItemRequest request);

    Page<ShareResponse> getSentShares(Pageable pageable);

    Page<SavedItemsResponse> getReceivedShares(Pageable pageable);

    SavedItemsResponse getReceivedSharedItem(Long savedItemId);

    void revoke(Long shareId);
}
