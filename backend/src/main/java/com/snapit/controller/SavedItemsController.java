package com.snapit.controller;

import com.snapit.dto.request.SavedItemsRequest;
import com.snapit.dto.request.FavoriteRequest;
import com.snapit.dto.response.SavedItemsResponse;
import com.snapit.dto.response.PageResponse;
import com.snapit.enums.MemoryType;
import com.snapit.service.SavedItemsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping(value = "/api/saved-items")
public class SavedItemsController {
    private static final int MAX_PAGE_SIZE = 100;
    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of("createdAt", "updatedAt", "title", "contentType", "favorite");

    @Autowired
    private SavedItemsService savedItemsService;

    @PostMapping
    public ResponseEntity<String> saveItems(@Valid @RequestBody SavedItemsRequest payLoad) {
        SavedItemsResponse savedItem = savedItemsService.saveItem(payLoad);
        String successMessage =
                "Item is successfully saved with ID : " + savedItem.getId();
        return new ResponseEntity<>(successMessage, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<PageResponse<SavedItemsResponse>> getAllSavedItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction
    ) {
        Pageable pageable = createPageable(page, size, sortBy, direction);
        return ResponseEntity.ok(PageResponse.from(savedItemsService.getAllSavedItems(pageable)));
    }

    @GetMapping("/favorites")
    public ResponseEntity<PageResponse<SavedItemsResponse>> getFavoriteSavedItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction
    ) {
        Pageable pageable = createPageable(page, size, sortBy, direction);
        return ResponseEntity.ok(PageResponse.from(savedItemsService.getFavoriteSavedItems(pageable)));
    }

    @GetMapping("/{Id}")
    public ResponseEntity<SavedItemsResponse> getSavedItemById(
            @PathVariable Long Id) {
        SavedItemsResponse savedItem = savedItemsService.getSavedItemById(Id);
        if (savedItem == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(savedItem);
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponse<SavedItemsResponse>> searchItems(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) MemoryType contentType,
            @RequestParam(required = false) Boolean favorite,
            @RequestParam(required = false) Long tagId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction
    ) {
        if (keyword != null && keyword.isBlank()
                && contentType == null && favorite == null && tagId == null) {
            throw new IllegalArgumentException("Search keyword cannot be blank");
        }
        Pageable pageable = createPageable(page, size, sortBy, direction);
        Page<SavedItemsResponse> result =
                savedItemsService.searchItems(keyword, contentType, favorite, tagId, pageable);
        return ResponseEntity.ok(PageResponse.from(result));
    }

    @PutMapping("/{id}/favorite")
    public ResponseEntity<SavedItemsResponse> updateFavorite(
            @PathVariable Long id,
            @Valid @RequestBody FavoriteRequest request
    ) {
        return ResponseEntity.ok(savedItemsService.updateFavorite(id, request.getFavorite()));
    }

    private Pageable createPageable(int page, int size, String sortBy, String direction) {
        if (page < 0 || size < 1 || size > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException(
                    "Page must be non-negative and size must be between 1 and " + MAX_PAGE_SIZE
            );
        }
        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new IllegalArgumentException("Unsupported sort field.");
        }
        try {
            return PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sortBy));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Sort direction must be ASC or DESC.", exception);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<SavedItemsResponse> updateSavedItem(
            @PathVariable Long id,
            @Valid @RequestBody SavedItemsRequest request
    ) {
        SavedItemsResponse response =
                savedItemsService.updateSavedItem(id, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @DeleteMapping("/{Id}")
    public ResponseEntity<String> deleteSavedItemById(@PathVariable Long Id) {
        savedItemsService.deleteSavedItemById(Id);
        String deleteMessage = "Item with ID : " + Id + " is successfully deleted.";
        return new ResponseEntity<>(deleteMessage, HttpStatus.OK);
    }

    @GetMapping("/{id}/file")
    public ResponseEntity<Resource> getSavedItemFile(@PathVariable Long id) {
        Resource file = savedItemsService.getOwnedFile(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment")
                .body(file);
    }

    @DeleteMapping("/{id}/file")
    public ResponseEntity<Void> deleteSavedItemFile(@PathVariable Long id) {
        savedItemsService.deleteOwnedFile(id);
        return ResponseEntity.noContent().build();
    }


}
