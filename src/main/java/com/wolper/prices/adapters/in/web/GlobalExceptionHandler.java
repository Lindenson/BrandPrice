package com.wolper.prices.adapters.in.web;

import com.wolper.prices.adapters.in.web.dto.ErrorResponse;
import com.wolper.prices.core.exceptions.InvalidPriceDataException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private ErrorResponse buildErrorResponse(HttpStatus status, String message, Map<String, String> fields) {
        return ErrorResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .fields(fields)
                .timestamp(Instant.now())
                .build();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> fields = new HashMap<>();
        ex.getConstraintViolations().forEach(violation ->
                fields.put(violation.getPropertyPath().toString(), violation.getMessage())
        );
        log.warn("Constraint violations: {}", fields);
        return ResponseEntity.badRequest().body(buildErrorResponse(HttpStatus.BAD_REQUEST, "Constraint violation", fields));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParams(MissingServletRequestParameterException ex) {
        Map<String, String> fields = Map.of(ex.getParameterName(),
                "Parameter '" + ex.getParameterName() + "' is required and missing");
        log.warn("Missing request parameter: {}", ex.getParameterName());
        return ResponseEntity.badRequest().body(buildErrorResponse(HttpStatus.BAD_REQUEST, "Missing required parameter", fields));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Map<String, String> fields = Map.of(ex.getName(),
                "Parameter '" + ex.getName() + "' should be of type " +
                        (ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "Unknown") +
                        ", but value '" + ex.getValue() + "' is invalid");
        log.warn("Type mismatch for parameter {}: expected {}, got {}", ex.getName(),
                ex.getRequiredType(), ex.getValue());
        return ResponseEntity.badRequest().body(buildErrorResponse(HttpStatus.BAD_REQUEST, "Type mismatch", fields));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(NoResourceFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), null));
    }

    @ExceptionHandler(InvalidPriceDataException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPriceData(InvalidPriceDataException ex) {
        log.error("InvalidPriceDataException caught: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Database or mapping error", null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        log.error("Unhandled exception: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), null));
    }
}
