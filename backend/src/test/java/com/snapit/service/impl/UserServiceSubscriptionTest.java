package com.snapit.service.impl;

import com.snapit.dto.request.RegisterRequest;
import com.snapit.entity.Subscription;
import com.snapit.entity.User;
import com.snapit.enums.SubscriptionPlan;
import com.snapit.repository.SubscriptionRepository;
import com.snapit.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceSubscriptionTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createsFreeSubscriptionWhenRegisteringUser() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("user@example.com");
        request.setUsername("snapper");
        request.setPassword("strong-password");
        when(passwordEncoder.encode("strong-password")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(5L);
            return user;
        });
        when(subscriptionRepository.save(any(Subscription.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        userService.registerUser(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertEquals("encoded", userCaptor.getValue().getPassword());
        verify(subscriptionRepository).save(org.mockito.ArgumentMatchers.argThat(subscription ->
                subscription.getUser().getId().equals(5L)
                        && subscription.getPlan() == SubscriptionPlan.FREE
                        && subscription.getStartedAt() != null
        ));
    }
}
