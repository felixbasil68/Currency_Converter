package com.currency.converter.exception;

public class InvalidCurrencyException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InvalidCurrencyException(String message) {
        super(message);
    }
}
