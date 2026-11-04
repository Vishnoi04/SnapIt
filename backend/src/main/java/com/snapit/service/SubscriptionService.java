package com.snapit.service;

import com.snapit.dto.request.SubscriptionPlanUpdateRequest;
import com.snapit.dto.response.SubscriptionResponse;

public interface SubscriptionService {
    SubscriptionResponse getCurrentSubscription();

    SubscriptionResponse updatePlan(Long userId, SubscriptionPlanUpdateRequest request);
}
