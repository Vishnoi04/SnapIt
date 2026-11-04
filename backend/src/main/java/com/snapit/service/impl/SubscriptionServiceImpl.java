package com.snapit.service.impl;

import com.snapit.dto.request.SubscriptionPlanUpdateRequest;
import com.snapit.dto.response.SubscriptionResponse;
import com.snapit.entity.Subscription;
import com.snapit.entity.User;
import com.snapit.enums.SubscriptionPlan;
import com.snapit.repository.SubscriptionRepository;
import com.snapit.repository.UserRepository;
import com.snapit.service.SubscriptionService;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    public SubscriptionServiceImpl(
            SubscriptionRepository subscriptionRepository,
            UserRepository userRepository
    ) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public SubscriptionResponse getCurrentSubscription() {
        User user = getCurrentUser();
        Subscription subscription = subscriptionRepository.findByUserId(user.getId()).orElse(null);

        SubscriptionResponse response = new SubscriptionResponse();
        if (subscription == null) {
            response.setPlan(SubscriptionPlan.FREE);
            response.setStartedAt(user.getCreatedAt());
            return response;
        }

        boolean premiumExpired = subscription.getPlan() == SubscriptionPlan.PREMIUM
                && subscription.getExpiresAt() != null
                && !subscription.getExpiresAt().isAfter(LocalDateTime.now());
        response.setPlan(premiumExpired ? SubscriptionPlan.FREE : subscription.getPlan());
        response.setStartedAt(subscription.getStartedAt());
        response.setExpiresAt(subscription.getExpiresAt());
        return response;
    }

    @Override
    @Transactional
    public SubscriptionResponse updatePlan(Long userId, SubscriptionPlanUpdateRequest request) {
        if (request.getPlan() == SubscriptionPlan.FREE && request.getExpiresAt() != null) {
            throw new IllegalArgumentException("Free subscriptions cannot have an expiration date.");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new com.snapit.exception.ResourceNotFoundException(
                        "User not found with id: " + userId
                ));
        Subscription subscription = subscriptionRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Subscription newSubscription = new Subscription();
                    newSubscription.setUser(user);
                    return newSubscription;
                });
        subscription.setPlan(request.getPlan());
        subscription.setStartedAt(LocalDateTime.now());
        subscription.setExpiresAt(request.getExpiresAt());
        Subscription saved = subscriptionRepository.save(subscription);

        SubscriptionResponse response = new SubscriptionResponse();
        response.setPlan(saved.getPlan());
        response.setStartedAt(saved.getStartedAt());
        response.setExpiresAt(saved.getExpiresAt());
        return response;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof User user)) {
            throw new AuthenticationCredentialsNotFoundException("Authenticated user not found.");
        }
        return user;
    }
}
