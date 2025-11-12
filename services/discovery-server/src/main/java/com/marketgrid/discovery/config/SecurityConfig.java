package com.marketgrid.discovery.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration  // Spring @Bean container
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())  // No CSRF for API (stateless)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/eureka/**").permitAll()  // Dashboard open
                .anyRequest().authenticated()  // Protect actuators
            )
            .httpBasic();  // Simple username/pass
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        var user = User.withDefaultPasswordEncoder()  // BCrypt auto
            .username("admin")
            .password("admin")  // Change in prod! Use env vars
            .roles("USER")
            .build();
        return new InMemoryUserDetailsManager(user);
    }
}