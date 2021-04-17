package com.exception;

public class InvalidAccountTypeException  extends Exception {
    public InvalidAccountTypeException(String errorMessage) {
        super(errorMessage);
    }
}
