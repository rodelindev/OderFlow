package com.rodelindev.adapter.in.rest;

import com.rodelindev.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(OrderNotFoundException ex){
        log.warn("Order not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(buildErrorBody(ex.getMessage()));
    }

    @ExceptionHandler({OrderAlreadyCancelledException.class, OrderAlreadyPaidException.class})
    public ResponseEntity<Map<String, Object>> handleConflict(OrderDomainException ex){
        log.warn("Order conflict: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(buildErrorBody(ex.getMessage()));
    }

    @ExceptionHandler(EmptyOrderException.class)
    public ResponseEntity<Map<String, Object>> handleEmpty(EmptyOrderException ex){
        log.warn("Order is empty: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(buildErrorBody("Order cannot be empty."));
    }

    @ExceptionHandler(OrderDomainException.class)
    public ResponseEntity<Map<String, Object>> handleDomainException(OrderDomainException ex){
        log.warn("Domain exception: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(buildErrorBody(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex){
        log.warn("Unexpected error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(buildErrorBody(ex.getMessage()));
    }

    private Map<String, Object> buildErrorBody(String message){
        return Map.of(
                "error", message,
                "timestamp", LocalDateTime.now().toString()
        );
    }
}
