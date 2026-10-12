package com.student.management.service;

import com.student.management.dto.request.LoginRequest;
import com.student.management.dto.request.RegisterRequest;
import com.student.management.dto.response.AuthResponse;
import com.student.management.dto.response.UserResponse;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse register(RegisterRequest request);
    UserResponse getCurrentUser(String email);
}
