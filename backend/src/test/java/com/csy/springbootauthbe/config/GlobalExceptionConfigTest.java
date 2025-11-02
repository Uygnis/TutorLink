package com.csy.springbootauthbe.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionConfigTest {

    private GlobalExceptionConfig config;

    @BeforeEach
    void setup() {
        config = new GlobalExceptionConfig();
    }

    @Test
    void handleDataIntegrity_returnsConflictResponse() {
        DataIntegrityViolationException ex =
                new DataIntegrityViolationException("Duplicate key error");
        ResponseEntity<?> response = config.handleDataIntegrity(ex);

        assertEquals(409, response.getStatusCodeValue());
        assertTrue(((Map<?, ?>) response.getBody()).get("message").toString()
                .contains("Duplicate key error"));
    }

    @Test
    void handleBadCredentials_returnsUnauthorizedResponse() {
        ResponseEntity<?> response = config.handleBadCredentials();

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Invalid email or password",
                ((Map<?, ?>) response.getBody()).get("message"));
    }

    @Test
    void handleRuntime_returnsForbiddenResponse() {
        RuntimeException ex = new RuntimeException("Account suspended");
        ResponseEntity<?> response = config.handleRuntime(ex);

        assertEquals(403, response.getStatusCodeValue());
        assertEquals("Account suspended",
                ((Map<?, ?>) response.getBody()).get("message"));
    }

    @Test
    void handleOtherExceptions_returnsInternalServerError() {
        ResponseEntity<?> response = config.handleOtherExceptions();

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("An unexpected error occurred",
                ((Map<?, ?>) response.getBody()).get("message"));
    }

    @Test
    void buildResponse_containsTimestampAndStatus() {
        // invoke via reflection
        var resp = config.handleRuntime(new RuntimeException("test message"));
        Map<?, ?> body = (Map<?, ?>) resp.getBody();

        assertNotNull(body.get("timestamp"));
        assertEquals(403, body.get("status"));
        assertEquals("Forbidden", body.get("error"));
        assertEquals("test message", body.get("message"));
    }
}
