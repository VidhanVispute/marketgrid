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
public class JwtAuthenticationFilter implements GlobalFilter {  // GlobalFilter for reactive

    private final JwtTokenProvider tokenProvider;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (path.startsWith("/auth/")) {  // Skip public auth routes
            return chain.filter(exchange);
        }

        Optional<String> tokenOpt = parseBearerToken(exchange.getRequest());
        if (tokenOpt.isEmpty()) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = tokenOpt.get();
        try {
            if (!tokenProvider.validateToken(token)) {
                log.warn("Invalid JWT for path: {} - Correlation: {}", path, MDC.get("correlationId"));
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String email = tokenProvider.getEmailFromToken(token);
            // Mutate request to propagate user info downstream
            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header("X-User-Email", email)
                .build();
            exchange = exchange.mutate().request(mutatedRequest).build();
            log.debug("JWT validated for user: {} - Correlation: {}", email, MDC.get("correlationId"));
        } catch (Exception e) {
            log.error("JWT filter error: {}", e.getMessage(), e);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }

    private Optional<String> parseBearerToken(ServerHttpRequest request) {
        String bearer = request.getHeaders().getFirst("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return Optional.of(bearer.substring(7));
        }
        return Optional.empty();
    }
}