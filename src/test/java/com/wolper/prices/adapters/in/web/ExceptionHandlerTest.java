package com.wolper.prices.adapters.in.web;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void testHandleConstraintViolation() {
        var violation = getViolationMocked();
        ConstraintViolationException ex = new ConstraintViolationException(Set.of(violation));
        var response = handler.handleConstraintViolation(ex);

        assertEquals(400, response.getStatusCode().value());

        Map<String, String> body = response.getBody();
        assertNotNull(body);
        assertEquals(1, body.size());
        assertEquals("must not be null", body.get("productId"));

        verify(violation, times(2)).getPropertyPath();
        verify(violation, times(2)).getMessage();
    }

    @Test
    void testHandleNoResourceFound() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        NoResourceFoundException ex = new NoResourceFoundException(HttpMethod.GET, "final", "localhost");

        ResponseEntity<Map<String, String>> response = handler.handleNoResourceFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().get("error").startsWith("No static resource localhost for request"));
    }

    @Test
    void testHandleMissingParams() {
        MissingServletRequestParameterException ex =
                new MissingServletRequestParameterException("param", "String");

        var response = handler.handleMissingParams(ex);
        assertEquals(400, response.getStatusCode().value());
        Map<String, String> body = response.getBody();
        assertNotNull(body);
        assertEquals("Parameter 'param' is required and missing", body.get("param"));
    }

    @Test
    void testHandleTypeMismatchWithType() {
        MethodArgumentTypeMismatchException ex =
                new MethodArgumentTypeMismatchException("abc", Integer.class, "param", null, null);

        var response = handler.handleTypeMismatch(ex);

        assertEquals(400, response.getStatusCode().value());
        Map<String, String> body = response.getBody();
        assertNotNull(body);
        assertEquals(
                "Parameter 'param' should be of type Integer, but value 'abc' is invalid",
                body.get("param")
        );
    }

    @Test
    void testHandleTypeMismatchWithoutType() {
        MethodArgumentTypeMismatchException ex =
                new MethodArgumentTypeMismatchException("abc", null, "param", null, null);

        var response = handler.handleTypeMismatch(ex);

        assertEquals(400, response.getStatusCode().value());
        Map<String, String> body = response.getBody();
        assertNotNull(body);
        assertEquals(
                "Parameter 'param' should be of type Unknown, but value 'abc' is invalid",
                body.get("param")
        );
    }

    // helper
    private static @NotNull ConstraintViolation<Object> getViolationMocked() {
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("productId");

        @SuppressWarnings("unchecked")
        ConstraintViolation<Object> violation = mock(ConstraintViolation.class);
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("must not be null");
        return violation;
    }

}


