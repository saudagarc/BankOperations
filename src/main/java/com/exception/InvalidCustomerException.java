package com.exception;

public class InvalidCustomerException extends Throwable {
    public InvalidCustomerException(String errorMessage) {
        super(errorMessage);
    }
}
