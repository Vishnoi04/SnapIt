package com.snapit.dto.response;

import java.time.LocalDateTime;

public class ShareResponse {
    private Long id;
    private Long savedItemId;
    private String savedItemTitle;
    private String recipientUsername;
    private LocalDateTime sharedAt;

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

    public String getRecipientUsername() {
        return recipientUsername;
    }

    public void setRecipientUsername(String recipientUsername) {
        this.recipientUsername = recipientUsername;
    }

    public LocalDateTime getSharedAt() {
        return sharedAt;
    }

    public void setSharedAt(LocalDateTime sharedAt) {
        this.sharedAt = sharedAt;
    }
}
