package com.marketgrid.auth.controller;

import com.marketgrid.auth.dto.request.LoginRequest;
import com.marketgrid.auth.dto.request.RegisterRequest;
import com.marketgrid.auth.dto.request.RefreshTokenRequest;
import com.marketgrid.auth.dto.response.JwtResponse;
import com.marketgrid.auth.dto.response.TokenRefreshResponse;
import com.marketgrid.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<JwtResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Registration request for {}", request.getEmail());
        JwtResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login attempt for {}", request.getEmail());
        JwtResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("Refreshing token for {}", request.getEmail());
        String newAccessToken = authService.refreshToken(request.getRefreshToken(), request.getEmail());
        return ResponseEntity.ok(new TokenRefreshResponse(newAccessToken, "Bearer"));
    }
}
