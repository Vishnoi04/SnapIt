package com.snapit.controller;

import com.snapit.dto.request.ShareSavedItemRequest;
import com.snapit.dto.response.SavedItemsResponse;
import com.snapit.dto.response.ShareResponse;
import com.snapit.dto.response.PageResponse;
import com.snapit.service.ShareService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shares")
public class ShareController {
    private static final int MAX_PAGE_SIZE = 100;
    private final ShareService shareService;

    public ShareController(ShareService shareService) {
        this.shareService = shareService;
    }

    @PostMapping
    public ResponseEntity<ShareResponse> share(@Valid @RequestBody ShareSavedItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(shareService.share(request));
    }

    @GetMapping("/sent")
    public ResponseEntity<PageResponse<ShareResponse>> getSent(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(PageResponse.from(shareService.getSentShares(createPageable(page, size))));
    }

    @GetMapping("/received")
    public ResponseEntity<PageResponse<SavedItemsResponse>> getReceived(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(PageResponse.from(shareService.getReceivedShares(createPageable(page, size))));
    }

    @GetMapping("/received/items/{savedItemId}")
    public ResponseEntity<SavedItemsResponse> getReceivedItem(@PathVariable Long savedItemId) {
        return ResponseEntity.ok(shareService.getReceivedSharedItem(savedItemId));
    }

    @DeleteMapping("/{shareId}")
    public ResponseEntity<Void> revoke(@PathVariable Long shareId) {
        shareService.revoke(shareId);
        return ResponseEntity.noContent().build();
    }

    private Pageable createPageable(int page, int size) {
        if (page < 0 || size < 1 || size > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException(
                    "Page must be non-negative and size must be between 1 and " + MAX_PAGE_SIZE
            );
        }
        return PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
    }
}
