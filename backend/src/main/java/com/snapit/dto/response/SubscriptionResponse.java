package com.snapit.dto.response;

import com.snapit.enums.SubscriptionPlan;

import java.time.LocalDateTime;

public class SubscriptionResponse {
    private SubscriptionPlan plan;
    private LocalDateTime startedAt;
    private LocalDateTime expiresAt;

    public SubscriptionPlan getPlan() {
        return plan;
    }

    public void setPlan(SubscriptionPlan plan) {
        this.plan = plan;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}
