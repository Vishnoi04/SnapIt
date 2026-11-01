package com.snapit.service.impl;

import com.snapit.dto.request.RegisterRequest;
import com.snapit.dto.response.UserResponse;
import com.snapit.entity.User;
import com.snapit.enums.Role;
import com.snapit.exception.DuplicateResourceException;
import com.snapit.repository.UserRepository;
import com.snapit.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserResponse registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())){
           throw new DuplicateResourceException("Email is already exists.",  new RuntimeException("Duplicate email"));
        }
        if (userRepository.existsByUsername(registerRequest.getUsername())){
            throw new DuplicateResourceException("Username already exists.",  new RuntimeException("Duplicate username"));
        }
        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setRole(Role.USER);
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        User savedUser = userRepository.save(user);
        UserResponse response = new UserResponse();

        response.setId(savedUser.getId());
        response.setUsername(savedUser.getUsername());
        response.setEmail(savedUser.getEmail());
        response.setRole(savedUser.getRole());
        response.setCreatedAt(savedUser.getCreatedAt());

        return response;
    }
}
