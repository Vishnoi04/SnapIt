package com.snapit.dto.response;

import java.time.LocalDateTime;

public class ReminderResponse {
    private Long id;
    private Long savedItemId;
    private String savedItemTitle;
    private String message;
    private LocalDateTime remindAt;
    private boolean completed;
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSavedItemId() {
        return savedItemId;
    }

    public void setSavedItemId(Long savedItemId) {
        this.savedItemId = savedItemId;
    }

    public String getSavedItemTitle() {
        return savedItemTitle;
    }

    public void setSavedItemTitle(String savedItemTitle) {
        this.savedItemTitle = savedItemTitle;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getRemindAt() {
        return remindAt;
    }

    public void setRemindAt(LocalDateTime remindAt) {
        this.remindAt = remindAt;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
