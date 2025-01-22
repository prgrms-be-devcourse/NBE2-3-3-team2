package com.example.letmovie.global.exception.exceptionClass.payment;

public class TooManyRequestsException extends RuntimeException {

    public TooManyRequestsException(String message) {
        super(message);
    }
}
