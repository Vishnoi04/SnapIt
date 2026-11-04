package com.snapit.controller;

import com.snapit.dto.request.ReminderCompletionRequest;
import com.snapit.dto.request.ReminderRequest;
import com.snapit.dto.response.ReminderResponse;
import com.snapit.dto.response.PageResponse;
import com.snapit.service.ReminderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reminders")
public class ReminderController {
    private static final int MAX_PAGE_SIZE = 100;
    private final ReminderService reminderService;

    public ReminderController(ReminderService reminderService) {
        this.reminderService = reminderService;
    }

    @PostMapping
    public ResponseEntity<ReminderResponse> create(@Valid @RequestBody ReminderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reminderService.create(request));
    }

    @GetMapping
    public ResponseEntity<PageResponse<ReminderResponse>> getAll(
            @RequestParam(required = false) Boolean completed,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(PageResponse.from(reminderService.getAll(completed, createPageable(page, size))));
    }

    @GetMapping("/due")
    public ResponseEntity<PageResponse<ReminderResponse>> getDue(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(PageResponse.from(reminderService.getDue(createPageable(page, size))));
    }

    @PutMapping("/{reminderId}")
    public ResponseEntity<ReminderResponse> update(
            @PathVariable Long reminderId,
            @Valid @RequestBody ReminderRequest request
    ) {
        return ResponseEntity.ok(reminderService.update(reminderId, request));
    }

    @PatchMapping("/{reminderId}/completion")
    public ResponseEntity<ReminderResponse> updateCompletion(
            @PathVariable Long reminderId,
            @Valid @RequestBody ReminderCompletionRequest request
    ) {
        return ResponseEntity.ok(reminderService.updateCompletion(reminderId, request.getCompleted()));
    }

    @DeleteMapping("/{reminderId}")
    public ResponseEntity<Void> delete(@PathVariable Long reminderId) {
        reminderService.delete(reminderId);
        return ResponseEntity.noContent().build();
    }

    private Pageable createPageable(int page, int size) {
        if (page < 0 || size < 1 || size > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException(
                    "Page must be non-negative and size must be between 1 and " + MAX_PAGE_SIZE
            );
        }
        return PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "remindAt"));
    }
}
