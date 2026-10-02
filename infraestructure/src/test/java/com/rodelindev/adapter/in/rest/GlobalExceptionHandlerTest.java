package com.rodelindev.adapter.in.rest;

import com.rodelindev.exception.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNotFound_shouldReturn404_whenOrderNotFound() {

        OrderNotFoundException ex = new OrderNotFoundException("order-1");

        ResponseEntity<Map<String, Object>> response = handler.handleNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Order with id [order-1] was not found.", response.getBody().get("error"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    void handleConflict_shouldReturn409_whenOrderAlreadyCancelled() {

        OrderAlreadyCancelledException ex = new OrderAlreadyCancelledException("order-1");

        ResponseEntity<Map<String, Object>> response = handler.handleConflict(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Order with id [order-1] has already been cancelled.",
                response.getBody().get("error"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    void handleConflict_shouldReturn409_whenOrderAlreadyPaid() {

        OrderAlreadyPaidException ex = new OrderAlreadyPaidException("order-1");

        ResponseEntity<Map<String, Object>> response = handler.handleConflict(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Order with id [order-1] has already been paid.",
                response.getBody().get("error"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    void handleEmpty_shouldReturn400_whenOrderIsEmpty() {

        EmptyOrderException ex = new EmptyOrderException();

        ResponseEntity<Map<String, Object>> response = handler.handleEmpty(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Order cannot be empty.", response.getBody().get("error"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    void handleDomainException_shouldReturn400_whenDomainRuleViolated() {

        OrderDomainException ex = new OrderDomainException("Cannot add items to an order in status: PAID");

        ResponseEntity<Map<String, Object>> response = handler.handleDomainException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Cannot add items to an order in status: PAID", response.getBody().get("error"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    void handleGeneric_shouldReturn500_whenUnexpectedError() {

        RuntimeException ex = new RuntimeException("boom");

        ResponseEntity<Map<String, Object>> response = handler.handleGeneric(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("boom", response.getBody().get("error"));
        assertNotNull(response.getBody().get("timestamp"));
    }
}
