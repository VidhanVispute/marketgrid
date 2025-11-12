package com.marketgrid.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class LoggingGlobalFilter implements GlobalFilter {
    private static final String CORRELATION_ID = "correlationId";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String correlationId = Optional.ofNullable(MDC.get(CORRELATION_ID))
            .orElseGet(() -> UUID.randomUUID().toString());
        MDC.put(CORRELATION_ID, correlationId);

        log.info("Request: {} {} - Correlation: {}", exchange.getRequest().getMethod(), exchange.getRequest().getURI(), correlationId);

        return chain.filter(exchange).doFinally(signal -> MDC.remove(CORRELATION_ID));
    }
}