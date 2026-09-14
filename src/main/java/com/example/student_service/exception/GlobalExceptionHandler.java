package com.example.student_service.exception;

import com.example.student_service.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        log.warn(
                "Resource not found. Path: {}, Message: {}",
                request.getRequestURI(),
                ex.getMessage()
        );

        return error(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        log.warn(
                "Invalid request. Path: {}, Message: {}",
                request.getRequestURI(),
                ex.getMessage()
        );

        return error(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, String> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        fieldError -> fieldError.getField(),
                        fieldError -> fieldError.getDefaultMessage(),
                        (existing, replacement) -> existing
                ));

        log.warn(
                "Validation failed. Path: {}, Errors: {}",
                request.getRequestURI(),
                validationErrors
        );

        return error(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                request,
                validationErrors
        );
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResource(
            DuplicateResourceException ex,
            HttpServletRequest request) {

        log.warn(
                "Duplicate resource. Path: {}, Message: {}",
                request.getRequestURI(),
                ex.getMessage()
        );

        return error(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception ex,
            HttpServletRequest request) {

        log.error(
                "Unexpected error occurred. Path: {}, Error: {}",
                request.getRequestURI(),
                ex.getMessage(),
                ex
        );

        String errorMessage = ex.getMessage();

        if (errorMessage == null || errorMessage.isBlank()) {
            errorMessage = "No detailed error message available";
        }

        String finalMessage = ex.getClass().getSimpleName() + ": " + errorMessage;

        return error(
                HttpStatus.INTERNAL_SERVER_ERROR,
                finalMessage,
                request
        );
    }

    private ResponseEntity<ErrorResponse> error(
            HttpStatus status,
            String message,
            HttpServletRequest request) {

        return ResponseEntity.status(status)
                .body(
                        ErrorResponse.of(
                                status,
                                message,
                                request.getRequestURI()
                        )
                );
    }

    private ResponseEntity<ErrorResponse> error(
            HttpStatus status,
            String message,
            HttpServletRequest request,
            Map<String, String> validationErrors) {

        return ResponseEntity.status(status)
                .body(
                        ErrorResponse.of(
                                status,
                                message,
                                request.getRequestURI(),
                                validationErrors
                        )
                );
    }
}