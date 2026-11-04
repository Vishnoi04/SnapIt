package com.snapit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ShareSavedItemRequest {
    @NotNull(message = "Saved item ID is required.")
    private Long savedItemId;

    @NotBlank(message = "Recipient username is required.")
    @Size(max = 100, message = "Recipient username must not exceed 100 characters.")
    private String recipientUsername;

    public Long getSavedItemId() {
        return savedItemId;
    }

    public void setSavedItemId(Long savedItemId) {
        this.savedItemId = savedItemId;
    }

    public String getRecipientUsername() {
        return recipientUsername;
    }

    public void setRecipientUsername(String recipientUsername) {
        this.recipientUsername = recipientUsername;
    }
}
