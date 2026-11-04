package com.snapit.controller;

import com.snapit.dto.request.CollectionRequest;
import com.snapit.dto.response.CollectionResponse;
import com.snapit.dto.response.SavedItemsResponse;
import com.snapit.dto.response.PageResponse;
import com.snapit.service.CollectionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/collections")
public class CollectionController {
    private static final int MAX_PAGE_SIZE = 100;
    private final CollectionService collectionService;

    public CollectionController(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    @PostMapping
    public ResponseEntity<CollectionResponse> create(@Valid @RequestBody CollectionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(collectionService.create(request));
    }

    @GetMapping
    public ResponseEntity<PageResponse<CollectionResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(PageResponse.from(collectionService.getAll(createPageable(page, size))));
    }

    @PutMapping("/{collectionId}")
    public ResponseEntity<CollectionResponse> update(
            @PathVariable Long collectionId,
            @Valid @RequestBody CollectionRequest request
    ) {
        return ResponseEntity.ok(collectionService.update(collectionId, request));
    }

    @DeleteMapping("/{collectionId}")
    public ResponseEntity<Void> delete(@PathVariable Long collectionId) {
        collectionService.delete(collectionId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{collectionId}/items/{savedItemId}")
    public ResponseEntity<Void> addItem(
            @PathVariable Long collectionId,
            @PathVariable Long savedItemId
    ) {
        collectionService.addItem(collectionId, savedItemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{collectionId}/items/{savedItemId}")
    public ResponseEntity<Void> removeItem(
            @PathVariable Long collectionId,
            @PathVariable Long savedItemId
    ) {
        collectionService.removeItem(collectionId, savedItemId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{collectionId}/items")
    public ResponseEntity<PageResponse<SavedItemsResponse>> getItems(
            @PathVariable Long collectionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                PageResponse.from(collectionService.getItems(collectionId, createPageable(page, size)))
        );
    }

    private Pageable createPageable(int page, int size) {
        if (page < 0 || size < 1 || size > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException(
                    "Page must be non-negative and size must be between 1 and " + MAX_PAGE_SIZE
            );
        }
        return PageRequest.of(page, size);
    }
}
