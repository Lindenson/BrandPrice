package com.wolper.prices.adapters.in.web;

import com.wolper.prices.adapters.in.web.dto.ErrorResponse;
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

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
        ResponseEntity<ErrorResponse> response = handler.handleConstraintViolation(ex);

        assertEquals(400, response.getStatusCode().value());

        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(400, body.getStatus());
        assertEquals("Bad Request", body.getError());
        assertEquals("Constraint violation", body.getMessage());
        assertNotNull(body.getFields());
        assertEquals(1, body.getFields().size());
        assertEquals("must not be null", body.getFields().get("productId"));

        verify(violation, times(2)).getPropertyPath();
        verify(violation, times(2)).getMessage();
    }

    @Test
    void testHandleNoResourceFound() {
        NoResourceFoundException ex = new NoResourceFoundException(HttpMethod.GET, "final", "localhost");

        ResponseEntity<ErrorResponse> response = handler.handleNoResourceFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(404, body.getStatus());
        assertEquals("Not Found", body.getError());
        assertEquals(ex.getMessage(), body.getMessage());
    }

    @Test
    void testHandleMissingParams() {
        MissingServletRequestParameterException ex =
                new MissingServletRequestParameterException("param", "String");

        ResponseEntity<ErrorResponse> response = handler.handleMissingParams(ex);
        assertEquals(400, response.getStatusCode().value());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(400, body.getStatus());
        assertEquals("Bad Request", body.getError());
        assertEquals("Missing required parameter", body.getMessage());
        assertEquals("Parameter 'param' is required and missing", body.getFields().get("param"));
    }

    @Test
    void testHandleTypeMismatchWithType() {
        MethodArgumentTypeMismatchException ex =
                new MethodArgumentTypeMismatchException("abc", Integer.class, "param", null, null);

        ResponseEntity<ErrorResponse> response = handler.handleTypeMismatch(ex);

        assertEquals(400, response.getStatusCode().value());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(400, body.getStatus());
        assertEquals("Bad Request", body.getError());
        assertEquals("Type mismatch", body.getMessage());
        assertEquals(
                "Parameter 'param' should be of type Integer, but value 'abc' is invalid",
                body.getFields().get("param")
        );
    }

    @Test
    void testHandleTypeMismatchWithoutType() {
        MethodArgumentTypeMismatchException ex =
                new MethodArgumentTypeMismatchException("abc", null, "param", null, null);

        ResponseEntity<ErrorResponse> response = handler.handleTypeMismatch(ex);

        assertEquals(400, response.getStatusCode().value());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(400, body.getStatus());
        assertEquals("Bad Request", body.getError());
        assertEquals("Type mismatch", body.getMessage());
        assertEquals(
                "Parameter 'param' should be of type Unknown, but value 'abc' is invalid",
                body.getFields().get("param")
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
