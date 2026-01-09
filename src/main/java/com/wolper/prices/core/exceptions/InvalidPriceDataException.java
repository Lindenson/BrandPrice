package com.wolper.prices.core.exceptions;


public class InvalidPriceDataException extends RuntimeException {

    public InvalidPriceDataException(String message) {
        super(message);
    }

    public InvalidPriceDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
