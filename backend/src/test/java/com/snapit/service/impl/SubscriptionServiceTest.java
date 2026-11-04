package com.snapit.service.impl;

import com.snapit.entity.Subscription;
import com.snapit.entity.User;
import com.snapit.dto.request.SubscriptionPlanUpdateRequest;
import com.snapit.enums.SubscriptionPlan;
import com.snapit.repository.SubscriptionRepository;
import com.snapit.repository.UserRepository;
import com.snapit.exception.ResourceNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {
    private static final long CURRENT_USER_ID = 101L;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SubscriptionServiceImpl subscriptionService;

    private User currentUser;

    @BeforeEach
    void authenticateCurrentUser() {
        currentUser = new User();
        currentUser.setId(CURRENT_USER_ID);
        currentUser.setCreatedAt(LocalDateTime.now().minusDays(5));
        SecurityContextHolder.getContext().setAuthentication(
                UsernamePasswordAuthenticationToken.authenticated(currentUser, null, java.util.List.of())
        );
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void defaultsExistingAccountWithoutSubscriptionToFree() {
        when(subscriptionRepository.findByUserId(CURRENT_USER_ID)).thenReturn(Optional.empty());

        var response = subscriptionService.getCurrentSubscription();

        assertEquals(SubscriptionPlan.FREE, response.getPlan());
        assertEquals(currentUser.getCreatedAt(), response.getStartedAt());
        verify(subscriptionRepository).findByUserId(CURRENT_USER_ID);
    }

    @Test
    void returnsCurrentUnexpiredPremiumPlan() {
        Subscription subscription = new Subscription();
        subscription.setPlan(SubscriptionPlan.PREMIUM);
        subscription.setStartedAt(LocalDateTime.now().minusDays(1));
        subscription.setExpiresAt(LocalDateTime.now().plusDays(1));
        when(subscriptionRepository.findByUserId(CURRENT_USER_ID)).thenReturn(Optional.of(subscription));

        var response = subscriptionService.getCurrentSubscription();

        assertEquals(SubscriptionPlan.PREMIUM, response.getPlan());
        assertEquals(subscription.getExpiresAt(), response.getExpiresAt());
    }

    @Test
    void expiredPremiumIsReportedAsFree() {
        Subscription subscription = new Subscription();
        subscription.setPlan(SubscriptionPlan.PREMIUM);
        subscription.setStartedAt(LocalDateTime.now().minusDays(10));
        subscription.setExpiresAt(LocalDateTime.now().minusDays(1));
        when(subscriptionRepository.findByUserId(CURRENT_USER_ID)).thenReturn(Optional.of(subscription));

        var response = subscriptionService.getCurrentSubscription();

        assertEquals(SubscriptionPlan.FREE, response.getPlan());
    }

    @Test
    void adminPlanUpdateCreatesPremiumSubscriptionForExistingUser() {
        User user = new User();
        user.setId(99L);
        when(userRepository.findById(99L)).thenReturn(Optional.of(user));
        when(subscriptionRepository.findByUserId(99L)).thenReturn(Optional.empty());
        when(subscriptionRepository.save(any(Subscription.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        SubscriptionPlanUpdateRequest request = new SubscriptionPlanUpdateRequest();
        request.setPlan(SubscriptionPlan.PREMIUM);
        request.setExpiresAt(LocalDateTime.now().plusMonths(1));

        var response = subscriptionService.updatePlan(99L, request);

        assertEquals(SubscriptionPlan.PREMIUM, response.getPlan());
        assertEquals(request.getExpiresAt(), response.getExpiresAt());
        verify(subscriptionRepository).save(org.mockito.ArgumentMatchers.argThat(subscription ->
                subscription.getUser() == user
                        && subscription.getPlan() == SubscriptionPlan.PREMIUM
        ));
    }

    @Test
    void freePlanCannotHaveExpiration() {
        SubscriptionPlanUpdateRequest request = new SubscriptionPlanUpdateRequest();
        request.setPlan(SubscriptionPlan.FREE);
        request.setExpiresAt(LocalDateTime.now().plusDays(1));

        assertThrows(IllegalArgumentException.class, () -> subscriptionService.updatePlan(99L, request));

        verify(userRepository, never()).findById(99L);
    }

    @Test
    void adminPlanUpdateRejectsUnknownUser() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        SubscriptionPlanUpdateRequest request = new SubscriptionPlanUpdateRequest();
        request.setPlan(SubscriptionPlan.FREE);

        assertThrows(ResourceNotFoundException.class, () -> subscriptionService.updatePlan(99L, request));

        verify(subscriptionRepository, never()).save(any(Subscription.class));
    }
}
