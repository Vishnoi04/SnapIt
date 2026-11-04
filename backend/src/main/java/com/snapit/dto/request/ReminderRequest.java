package com.snapit.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class ReminderRequest {
    @NotNull(message = "Saved item ID is required.")
    private Long savedItemId;

    @Future(message = "Reminder time must be in the future.")
    @NotNull(message = "Reminder time is required.")
    private LocalDateTime remindAt;

    @Size(max = 500, message = "Reminder message must not exceed 500 characters.")
    private String message;

    public Long getSavedItemId() {
        return savedItemId;
    }

    public void setSavedItemId(Long savedItemId) {
        this.savedItemId = savedItemId;
    }

    public LocalDateTime getRemindAt() {
        return remindAt;
    }

    public void setRemindAt(LocalDateTime remindAt) {
        this.remindAt = remindAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
