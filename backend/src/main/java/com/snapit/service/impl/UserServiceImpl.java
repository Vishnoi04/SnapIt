package com.snapit.service.impl;

import com.snapit.dto.request.RegisterRequest;
import com.snapit.dto.response.UserResponse;
import com.snapit.entity.User;
import com.snapit.enums.Role;
import com.snapit.enums.SubscriptionPlan;
import com.snapit.exception.DuplicateResourceException;
import com.snapit.entity.Subscription;
import com.snapit.repository.SubscriptionRepository;
import com.snapit.repository.UserRepository;
import com.snapit.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(
            UserRepository userRepository,
            SubscriptionRepository subscriptionRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserResponse registerUser(RegisterRequest registerRequest) {
        String email = registerRequest.getEmail().trim().toLowerCase(Locale.ROOT);
        String username = registerRequest.getUsername().trim();
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Email is already exists.", new RuntimeException("Duplicate email"));
        }
        if (userRepository.existsByUsername(username)) {
            throw new DuplicateResourceException("Username already exists.", new RuntimeException("Duplicate username"));
        }
        User user = new User();
        user.setEmail(email);
        user.setRole(Role.USER);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        Subscription subscription = new Subscription();
        subscription.setUser(savedUser);
        subscription.setPlan(SubscriptionPlan.FREE);
        subscription.setStartedAt(savedUser.getCreatedAt());
        subscriptionRepository.save(subscription);
        UserResponse response = new UserResponse();

        response.setId(savedUser.getId());
        response.setUsername(savedUser.getUsername());
        response.setEmail(savedUser.getEmail());
        response.setRole(savedUser.getRole());
        response.setCreatedAt(savedUser.getCreatedAt());

        return response;
    }
}
