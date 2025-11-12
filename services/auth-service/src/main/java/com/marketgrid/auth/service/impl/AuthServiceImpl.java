package com.marketgrid.auth.service.impl;

import com.marketgrid.auth.dto.request.LoginRequest;
import com.marketgrid.auth.dto.request.RegisterRequest;
import com.marketgrid.auth.dto.response.JwtResponse;
import com.marketgrid.auth.exception.EmailAlreadyExistsException;
import com.marketgrid.auth.model.entity.User;
import com.marketgrid.auth.repository.UserRepository;
import com.marketgrid.auth.security.JwtTokenProvider;
import com.marketgrid.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public JwtResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists: " + request.getEmail());
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(validateRole(request.getRole()))
                .build();

        userRepository.save(user);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtTokenProvider.generateToken(authentication);
        log.info("User registered successfully: {}", user.getEmail());

        return JwtResponse.of(jwt, user.getId(), user.getEmail(), List.of(user.getRole()));
    }

    @Override
    public JwtResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        String jwt = jwtTokenProvider.generateToken(authentication);
        log.info("User logged in: {}", user.getEmail());

        return JwtResponse.of(jwt, user.getId(), user.getEmail(), List.of(user.getRole()));
    }

    @Override
    public String refreshToken(String refreshToken, String email) {
    if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired refresh token");
    }
    String tokenEmail = jwtTokenProvider.getEmailFromToken(refreshToken);  // Reuse getEmailFromToken for refresh too
    if (!tokenEmail.equals(email)) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email mismatch in refresh token");
    }
    return jwtTokenProvider.generateTokenFromRefresh(refreshToken);
}

    private String validateRole(String role) {
        return switch (role.toUpperCase()) {
            case "ADMIN", "VENDOR", "CUSTOMER" -> role.toUpperCase();
            default -> "CUSTOMER";
        };
    }
}
