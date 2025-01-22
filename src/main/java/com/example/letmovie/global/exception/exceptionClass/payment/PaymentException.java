package com.example.letmovie.global.exception.exceptionClass.payment;

import com.example.letmovie.global.exception.ErrorCodes;
import lombok.Getter;

@Getter
public class PaymentException extends RuntimeException {

    private final ErrorCodes errorCode;

    public PaymentException(ErrorCodes errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
