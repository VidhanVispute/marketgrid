package com.marketgrid.auth.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
@ToString
public class JwtResponse {

    private final String token;
    private final String type;
    private final Long id;
    private final String email;
    private final List<String> roles;

    public static JwtResponse of(String token, Long id, String email, List<String> roles) {
        return JwtResponse.builder()
                .token(token)
                .type("Bearer")
                .id(id)
                .email(email)
                .roles(roles)
                .build();
    }
}
