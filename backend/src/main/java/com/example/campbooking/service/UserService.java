package com.example.campbooking.service;

import com.example.campbooking.dto.LoginRequest;
import com.example.campbooking.dto.RegisterRequest;
import java.util.Map;

public interface UserService {
    Map<String, Object> register(RegisterRequest request);
    Map<String, Object> login(LoginRequest request);
    Map<String, Object> getCurrentUser(Long userId);
}
