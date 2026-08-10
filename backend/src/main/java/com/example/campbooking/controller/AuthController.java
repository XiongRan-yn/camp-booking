package com.example.campbooking.controller;

import com.example.campbooking.common.Result;
import com.example.campbooking.dto.LoginRequest;
import com.example.campbooking.dto.RegisterRequest;
import com.example.campbooking.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Result<Map<String, Object>> register(@RequestBody RegisterRequest request) {
        Map<String, Object> user = userService.register(request);
        return Result.success(user);
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest request) {
        Map<String, Object> data = userService.login(request);
        return Result.success(data);
    }

    @GetMapping("/me")
    public Result<Map<String, Object>> me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) auth.getPrincipal();
        Map<String, Object> user = userService.getCurrentUser(userId);
        return Result.success(user);
    }
}
