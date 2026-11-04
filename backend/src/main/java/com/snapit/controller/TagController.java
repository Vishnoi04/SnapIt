package com.snapit.controller;

import com.snapit.dto.request.TagRequest;
import com.snapit.dto.response.SavedItemsResponse;
import com.snapit.dto.response.PageResponse;
import com.snapit.dto.response.TagResponse;
import com.snapit.service.TagService;
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
@RequestMapping("/api/tags")
public class TagController {
    private static final int MAX_PAGE_SIZE = 100;
    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @PostMapping
    public ResponseEntity<TagResponse> create(@Valid @RequestBody TagRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tagService.create(request));
    }

    @GetMapping
    public ResponseEntity<PageResponse<TagResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(PageResponse.from(tagService.getAll(createPageable(page, size))));
    }

    @PutMapping("/{tagId}")
    public ResponseEntity<TagResponse> update(
            @PathVariable Long tagId,
            @Valid @RequestBody TagRequest request
    ) {
        return ResponseEntity.ok(tagService.update(tagId, request));
    }

    @DeleteMapping("/{tagId}")
    public ResponseEntity<Void> delete(@PathVariable Long tagId) {
        tagService.delete(tagId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{tagId}/items/{savedItemId}")
    public ResponseEntity<Void> addItem(
            @PathVariable Long tagId,
            @PathVariable Long savedItemId
    ) {
        tagService.addItem(tagId, savedItemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{tagId}/items/{savedItemId}")
    public ResponseEntity<Void> removeItem(
            @PathVariable Long tagId,
            @PathVariable Long savedItemId
    ) {
        tagService.removeItem(tagId, savedItemId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{tagId}/items")
    public ResponseEntity<PageResponse<SavedItemsResponse>> getItems(
            @PathVariable Long tagId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(PageResponse.from(tagService.getItems(tagId, createPageable(page, size))));
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
