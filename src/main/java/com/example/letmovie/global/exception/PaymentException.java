package com.example.letmovie.global.exception;

import lombok.Getter;

@Getter
public class PaymentException extends RuntimeException {

    private final ErrorCodes errorCode;

    public PaymentException(ErrorCodes errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}
