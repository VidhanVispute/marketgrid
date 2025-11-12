package com.marketgrid.auth.service;

import com.marketgrid.auth.dto.request.LoginRequest;
import com.marketgrid.auth.dto.request.RegisterRequest;
import com.marketgrid.auth.dto.response.JwtResponse;

public interface AuthService {
    JwtResponse register(RegisterRequest request);
    JwtResponse login(LoginRequest request);
    String refreshToken(String refreshToken, String email);
}