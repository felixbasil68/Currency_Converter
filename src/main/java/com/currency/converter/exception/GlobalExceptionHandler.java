package com.currency.converter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.time.LocalDateTime;

/**
 * Global exception handler for the currency converter application.
 * Handles specific and generic exceptions and returns a structured error
 * response.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles exceptions of type InvalidCurrencyException.
     *
     * @param ex the thrown InvalidCurrencyException
     * @return a ResponseEntity with BAD_REQUEST status and error details
     */
    @ExceptionHandler(InvalidCurrencyException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCurrency(InvalidCurrencyException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Handles exceptions of type ApiUnavailableException.
     *
     * @param ex the thrown ApiUnavailableException
     * @return a ResponseEntity with SERVICE_UNAVAILABLE status and error details
     */
    @ExceptionHandler(ApiUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleApiUnavailable(ApiUnavailableException ex) {
        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
    }

    /**
     * Handles all other generic exceptions.
     *
     * @param ex the thrown Exception
     * @return a ResponseEntity with INTERNAL_SERVER_ERROR status and a generic
     *         error message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
    }

    /**
     * Builds and returns a standardized error response.
     *
     * @param status  the HTTP status to be returned
     * @param message the error message to be included
     * @return a ResponseEntity containing the ErrorResponse
     */
    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message);
        return new ResponseEntity<>(error, status);
    }
}
