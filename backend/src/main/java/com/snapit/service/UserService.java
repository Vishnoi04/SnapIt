package com.snapit.service;

import com.snapit.dto.request.RegisterRequest;
import com.snapit.dto.response.UserResponse;

public interface UserService {
    UserResponse registerUser(RegisterRequest registerRequest);
}
