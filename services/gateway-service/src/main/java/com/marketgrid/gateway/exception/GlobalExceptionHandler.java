package com.marketgrid.gateway.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GatewayException.class)
    public ResponseEntity<Map<String, Object>> handleGatewayException(GatewayException ex) {
        log.error("Gateway error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(ex.getStatus()).body(
            Map.of(
                "timestamp", LocalDateTime.now(),
                "status", ex.getStatus().value(),
                "error", "Gateway Error",
                "message", ex.getMessage()
            )
        );
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatus(ResponseStatusException ex) {
        log.warn("Status exception: {}", ex.getReason());
        return ResponseEntity.status(ex.getStatusCode()).body(
            Map.of(
                "timestamp", LocalDateTime.now(),
                "status", ex.getStatusCode().value(),
                "error", ex.getReason(),
                "message", ex.getMessage()
            )
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        log.error("Unhandled gateway exception", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "error", "Internal Server Error",
                "message", "Gateway unavailable"
            )
        );
    }
}