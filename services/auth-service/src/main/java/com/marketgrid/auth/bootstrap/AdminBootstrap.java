package com.marketgrid.auth.bootstrap;

import com.marketgrid.auth.model.entity.User;
import com.marketgrid.auth.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminBootstrap {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.email:admin@marketgrid.com}")
    private String adminEmail;

    @Value("${admin.password:admin123}")
    private String adminPassword;

    @PostConstruct
    public void initAdmin() {
        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = new User();
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole("ADMIN");
            userRepository.save(admin);
            log.warn("Bootstrap admin created -> email: {}, password: {}", adminEmail, adminPassword);
        } else {
            log.info("Admin user already exists: {}", adminEmail);
        }
    }
}
