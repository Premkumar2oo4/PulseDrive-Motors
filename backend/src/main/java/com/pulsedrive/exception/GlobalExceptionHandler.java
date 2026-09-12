package com.pulsedrive.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.dao.DataIntegrityViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleResourceNotFound(
                        ResourceNotFoundException exception) {

                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.NOT_FOUND.value(),
                                exception.getMessage(),
                                LocalDateTime.now());

                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(errorResponse);
        }

        @ExceptionHandler(DuplicateResourceException.class)
        public ResponseEntity<ErrorResponse> handleDuplicateResource(
                        DuplicateResourceException exception) {

                ErrorResponse errorResponse = new ErrorResponse(
                                HttpStatus.CONFLICT.value(),
                                exception.getMessage(),
                                LocalDateTime.now());

                return ResponseEntity
                                .status(HttpStatus.CONFLICT)
                                .body(errorResponse);
        }
        @ExceptionHandler(InvalidCredentialsException.class)
public ResponseEntity<ErrorResponse> handleInvalidCredentials(
        InvalidCredentialsException exception) {

    ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.UNAUTHORIZED.value(),
            exception.getMessage(),
            LocalDateTime.now()
    );

    return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(errorResponse);
}
@ExceptionHandler(IllegalArgumentException.class)
public ResponseEntity<ErrorResponse> handleBadRequest(
        IllegalArgumentException exception) {

    ErrorResponse errorResponse =
            new ErrorResponse(
                    HttpStatus.BAD_REQUEST.value(),
                    exception.getMessage(),
                    LocalDateTime.now()
            );

    return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errorResponse);
}

@ExceptionHandler(DataIntegrityViolationException.class)
public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
        DataIntegrityViolationException exception) {

    ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Image could not be saved. Check that the vehicle exists and the image details are valid.",
            LocalDateTime.now());

    return ResponseEntity
            .badRequest()
            .body(errorResponse);
}
@ExceptionHandler(
        org.springframework.web.bind.MethodArgumentNotValidException.class
)
public ResponseEntity<Map<String, Object>>
handleValidationErrors(
        org.springframework.web.bind.MethodArgumentNotValidException exception) {

    Map<String, String> errors =
            new HashMap<>();

    exception.getBindingResult()
            .getFieldErrors()
            .forEach(error ->
                    errors.put(
                            error.getField(),
                            error.getDefaultMessage()
                    )
            );

    Map<String, Object> response =
            new HashMap<>();

    response.put("status", 400);
    response.put("message", "Validation failed");
    response.put("errors", errors);
    response.put(
            "timestamp",
            LocalDateTime.now()
    );

    return ResponseEntity
            .badRequest()
            .body(response);
}
@ExceptionHandler(Exception.class)
public ResponseEntity<Map<String, Object>>
handleGenericException(Exception exception) {

    Map<String, Object> response = new HashMap<>();

    response.put("status", 500);
    response.put(
            "message",
            "An unexpected error occurred"
    );
    response.put(
            "timestamp",
            LocalDateTime.now()
    );

    return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(response);
}
}