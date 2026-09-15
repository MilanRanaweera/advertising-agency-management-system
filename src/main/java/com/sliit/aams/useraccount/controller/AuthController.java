package com.sliit.aams.useraccount.controller;


import com.sliit.aams.common.exception.ApiResponse;
import com.sliit.aams.useraccount.dto.AuthResponse;
import com.sliit.aams.useraccount.dto.LoginRequest;
import com.sliit.aams.useraccount.dto.RegisterRequest;
import com.sliit.aams.useraccount.service.UserAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserAccountService svc;

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest r) {
        return ApiResponse.ok("Registered successfully", svc.register(r));
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest r) {
        return ApiResponse.ok("Login successful", svc.login(r));
    }
}