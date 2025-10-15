package com.example.schoolmoney.exception;

import com.example.schoolmoney.common.dto.ApiErrorResponseDto;
import com.example.schoolmoney.utils.SnakeCaseConverter;
import com.stripe.exception.StripeException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponseDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();

        e.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(SnakeCaseConverter.convertToSnakeCase(error.getField()), error.getDefaultMessage())
        );

        ApiErrorResponseDto response = ApiErrorResponseDto.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Validation error")
                .errors(errors)
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponseDto> handleGlobalException(Exception e) {
        ApiErrorResponseDto response = ApiErrorResponseDto.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Unexpected error occurred: " + e.getMessage())
                .build();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    @ExceptionHandler(MailException.class)
    public ResponseEntity<ApiErrorResponseDto> handleMailException(MailException e) {
        ApiErrorResponseDto response = ApiErrorResponseDto.builder()
                .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                .message("Email could not be sent. Please try again later.")
                .build();

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response);
    }

    @ExceptionHandler(StripeException.class)
    public ResponseEntity<ApiErrorResponseDto> handleStripeException(StripeException e) {
        ApiErrorResponseDto response = ApiErrorResponseDto.builder()
                .status(HttpStatus.BAD_GATEWAY.value())
                .message("A payment processing error occurred. Please try again or contact support.")
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponseDto> handleAccessDeniedException(AccessDeniedException e) {
        ApiErrorResponseDto response = ApiErrorResponseDto.builder()
                .status(HttpStatus.FORBIDDEN.value())
                .message("Access denied: " + e.getMessage())
                .build();

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(response);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiErrorResponseDto> handleEntityNotFoundException(EntityNotFoundException e) {
        ApiErrorResponseDto response = ApiErrorResponseDto.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(e.getMessage())
                .build();

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<ApiErrorResponseDto> handleEntityExistsException(EntityExistsException e) {
        ApiErrorResponseDto response = ApiErrorResponseDto.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiErrorResponseDto> handleUsernameNotFoundException(UsernameNotFoundException e) {
        ApiErrorResponseDto response = ApiErrorResponseDto.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(e.getMessage())
                .build();

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponseDto> handleIllegalArgumentException(IllegalArgumentException e) {
        ApiErrorResponseDto response = ApiErrorResponseDto.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponseDto> handleIllegalStateException(IllegalStateException e) {
        ApiErrorResponseDto response = ApiErrorResponseDto.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

}
