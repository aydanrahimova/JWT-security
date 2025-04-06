package com.example.securityjwt.controller;

import com.example.securityjwt.dto.request.AuthRequest;
import com.example.securityjwt.dto.response.AuthResponse;
import com.example.securityjwt.dto.request.RefreshTokenRequest;
import com.example.securityjwt.dto.request.RegisterRequest;
import com.example.securityjwt.service.AuthService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {
    AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh-token")
    public AuthResponse refreshAccessToken(@RequestBody RefreshTokenRequest tokenRequest) {
        return authService.refreshAccessToken(tokenRequest);
    }
}
