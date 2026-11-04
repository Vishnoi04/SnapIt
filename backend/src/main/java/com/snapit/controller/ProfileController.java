package com.snapit.controller;

import com.snapit.dto.response.PublicProfileResponse;
import com.snapit.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {
    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/{username}")
    public ResponseEntity<PublicProfileResponse> getProfile(@PathVariable String username) {
        return ResponseEntity.ok(profileService.getProfile(username));
    }
}
