package com.snapit.service;

import com.snapit.dto.request.LoginRequest;
import com.snapit.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse login(LoginRequest loginRequest);
}
