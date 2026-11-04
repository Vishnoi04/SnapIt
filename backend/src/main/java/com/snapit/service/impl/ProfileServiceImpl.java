package com.snapit.service.impl;

import com.snapit.dto.response.PublicProfileResponse;
import com.snapit.entity.User;
import com.snapit.exception.ResourceNotFoundException;
import com.snapit.repository.UserRepository;
import com.snapit.service.ProfileService;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class ProfileServiceImpl implements ProfileService {
    private final UserRepository userRepository;

    public ProfileServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public PublicProfileResponse getProfile(String username) {
        requireAuthenticatedUser();
        User user = userRepository.findByUsername(username.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found."));
        PublicProfileResponse response = new PublicProfileResponse();
        response.setUsername(user.getUsername());
        response.setMemberSince(user.getCreatedAt());
        return response;
    }

    private void requireAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof User)) {
            throw new AuthenticationCredentialsNotFoundException("Authenticated user not found.");
        }
    }
}
