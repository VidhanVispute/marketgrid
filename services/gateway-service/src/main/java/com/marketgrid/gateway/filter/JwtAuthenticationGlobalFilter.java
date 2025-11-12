package com.marketgrid.gateway.filter;

import com.marketgrid.gateway.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationGlobalFilter implements GlobalFilter {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (path.startsWith("/auth/")) {
            return chain.filter(exchange);
        }

        Optional<String> tokenOpt = extractToken(exchange.getRequest());
        if (tokenOpt.isEmpty()) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = tokenOpt.get();
        if (!jwtTokenProvider.validateToken(token)) {
            log.warn("Invalid JWT for path: {} - Correlation: {}", path, MDC.get("correlationId"));
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String email = jwtTokenProvider.getEmailFromToken(token);
        exchange.getRequest().mutate().header("X-User-Email", email).build();
        log.debug("JWT validated for user: {} - Correlation: {}", email, MDC.get("correlationId"));
        return chain.filter(exchange);
    }

    private Optional<String> extractToken(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return Optional.of(authHeader.substring(7));
        }
        return Optional.empty();
    }
}