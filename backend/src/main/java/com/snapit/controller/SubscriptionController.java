package com.snapit.controller;

import com.snapit.dto.request.SubscriptionPlanUpdateRequest;
import com.snapit.dto.response.SubscriptionResponse;
import com.snapit.service.SubscriptionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping("/me")
    public ResponseEntity<SubscriptionResponse> getCurrentSubscription() {
        return ResponseEntity.ok(subscriptionService.getCurrentSubscription());
    }

    @PutMapping("/{userId}")
    public ResponseEntity<SubscriptionResponse> updatePlan(
            @PathVariable Long userId,
            @Valid @RequestBody SubscriptionPlanUpdateRequest request
    ) {
        return ResponseEntity.ok(subscriptionService.updatePlan(userId, request));
    }
}
