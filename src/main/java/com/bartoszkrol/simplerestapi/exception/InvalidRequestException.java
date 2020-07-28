package com.bartoszkrol.simplerestapi.exception;

public class InvalidRequestException extends Exception {

    public InvalidRequestException(String message) {
        super(message);
    }
}
