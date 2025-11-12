package com.marketgrid.auth.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiry}")
    private long jwtExpiry;

    @Value("${jwt.refresh-expiry}")
    private long refreshExpiry;

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtExpiry);

        return Jwts.builder()
                .subject(principal.getUsername())
                .claim("roles", principal.getAuthorities()
                        .stream()
                        .map(Object::toString)
                        .collect(Collectors.toList()))
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();
    }

    public String generateRefreshToken(Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshExpiry);

        return Jwts.builder()
                .subject(principal.getUsername())
                .claim("roles", principal.getAuthorities()
                        .stream()
                        .map(Object::toString)
                        .collect(Collectors.toList()))
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.err.println("JWT expired: " + e.getMessage());
        } catch (JwtException | IllegalArgumentException e) {
            System.err.println("Invalid JWT: " + e.getMessage());
        }
        return false;
    }

    public boolean validateRefreshToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.err.println("Refresh token expired: " + e.getMessage());
        } catch (JwtException | IllegalArgumentException e) {
            System.err.println("Invalid refresh token: " + e.getMessage());
        }
        return false;
    }

    public String generateTokenFromRefresh(String refreshToken) {
    Claims claims = Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(refreshToken).getPayload();
    String email = claims.getSubject();
    @SuppressWarnings("unchecked")
    List<String> roles = (List<String>) claims.get("roles");  // Safe cast
    Date now = new Date();
    Date expiry = new Date(now.getTime() + jwtExpiry);

    return Jwts.builder()
        .subject(email)
        .claim("roles", roles)
        .issuedAt(now)
        .expiration(expiry)
        .signWith(getSigningKey(), Jwts.SIG.HS512)
        .compact();
}

    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}