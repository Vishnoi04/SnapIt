package com.snapit.dto.request;

import jakarta.validation.constraints.NotNull;

public class ReminderCompletionRequest {
    @NotNull(message = "Completion state is required.")
    private Boolean completed;

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
}
