package com.snapit.dto.request;

import com.snapit.enums.SubscriptionPlan;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class SubscriptionPlanUpdateRequest {
    @NotNull(message = "Subscription plan is required.")
    private SubscriptionPlan plan;

    @Future(message = "Subscription expiration must be in the future.")
    private LocalDateTime expiresAt;

    public SubscriptionPlan getPlan() {
        return plan;
    }

    public void setPlan(SubscriptionPlan plan) {
        this.plan = plan;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}
