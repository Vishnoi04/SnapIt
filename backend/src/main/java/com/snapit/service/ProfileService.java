package com.snapit.service;

import com.snapit.dto.response.PublicProfileResponse;

public interface ProfileService {
    PublicProfileResponse getProfile(String username);
}
