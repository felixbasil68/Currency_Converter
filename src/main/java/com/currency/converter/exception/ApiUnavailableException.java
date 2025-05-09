package com.currency.converter.exception;

public class ApiUnavailableException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ApiUnavailableException(String message) {
        super(message);
    }
}
