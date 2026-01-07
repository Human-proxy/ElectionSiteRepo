package nl.hva.dederdekamer.election_backend.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.*;
/**
 * Centralized exception handler producing consistent JSON error bodies.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 401 for authentication failures */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<?> handleUnauthorized(UnauthorizedException e, HttpServletRequest req) {
        return body(HttpStatus.UNAUTHORIZED, "Unauthorized", e.getMessage(), req.getRequestURI(), null);
    }

    /** 400 for invalid arguments */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException e, HttpServletRequest req) {
        return body(HttpStatus.BAD_REQUEST, "Bad Request", e.getMessage(), req.getRequestURI(), null);
    }

    /** 400 for @Valid request bodies with field errors */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpServletRequest req) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fe : e.getBindingResult().getFieldErrors()) {
            errors.put(fe.getField(), fe.getDefaultMessage());
        }
        return body(HttpStatus.BAD_REQUEST, "Validation Failed", "One or more fields are invalid",
                req.getRequestURI(), errors);
    }

    /** 400 for @Validated on query/path params */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolation(ConstraintViolationException e, HttpServletRequest req) {
        Map<String, String> errors = new LinkedHashMap<>();
        e.getConstraintViolations().forEach(v -> errors.put(v.getPropertyPath().toString(), v.getMessage()));
        return body(HttpStatus.BAD_REQUEST, "Validation Failed", "One or more parameters are invalid",
                req.getRequestURI(), errors);
    }

    /** 404 for repositories/service lookups that use Optional.orElseThrow() */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleNotFound(NoSuchElementException e, HttpServletRequest req) {
        return body(HttpStatus.NOT_FOUND, "Not Found", e.getMessage(), req.getRequestURI(), null);
    }

    /** 500 fallback */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception e, HttpServletRequest req) {
        return body(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error", e.getMessage(), req.getRequestURI(), null);
    }

    // ---- small helper ----
    private ResponseEntity<?> body(HttpStatus status, String error, String message,
                                   String path, Map<String, ?> details) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("status", status.value());
        map.put("error", error);
        map.put("message", message);
        map.put("path", path);
        if (details != null && !details.isEmpty()) {
            map.put("details", details);
        }
        return ResponseEntity.status(status).body(map);
    }

    /**
     * Handle ResourceNotFoundException (404 Not Found)
     * For municipality/party/election data not found
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError(req.getRequestURI(), 404, "Not Found", ex.getMessage()));
    }

    /**
     * Handle InvalidRequestException (400 Bad Request)
     * For invalid election IDs, municipality names, etc.
     */
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ApiError> handleInvalidRequest(InvalidRequestException ex, HttpServletRequest req) {
        return ResponseEntity.badRequest()
                .body(new ApiError(req.getRequestURI(), 400, "Bad Request", ex.getMessage()));
    }
}
