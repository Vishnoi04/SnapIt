package com.snapit.service.impl;

import com.snapit.entity.User;
import com.snapit.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProfileServiceImpl profileService;

    @BeforeEach
    void authenticateCurrentUser() {
        User currentUser = new User();
        currentUser.setId(10L);
        SecurityContextHolder.getContext().setAuthentication(
                UsernamePasswordAuthenticationToken.authenticated(currentUser, null, java.util.List.of())
        );
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void returnsOnlyPublicProfileFields() {
        User user = new User();
        user.setUsername("snapper");
        user.setEmail("private@example.com");
        when(userRepository.findByUsername("snapper")).thenReturn(Optional.of(user));

        var response = profileService.getProfile(" snapper ");

        assertEquals("snapper", response.getUsername());
        verify(userRepository).findByUsername("snapper");
    }
}
