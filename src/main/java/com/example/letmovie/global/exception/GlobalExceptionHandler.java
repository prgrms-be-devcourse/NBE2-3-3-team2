package com.example.letmovie.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ErrorResponse> handlePaymentException(PaymentException e) {
        ErrorCodes error = e.getErrorCode();
        return ResponseEntity.status(error.getHttpStatus())
                .body(new ErrorResponse(error.getMessage(), error.getHttpStatus().toString()));
    }

    @ExceptionHandler(VerificationCodeException.class)
    public ResponseEntity<ErrorResponse> handleVerificationCodeException(VerificationCodeException e) {
        ErrorCodes error = e.getErrorCode();
        return ResponseEntity.status(error.getHttpStatus())
                .body(new ErrorResponse(error.getMessage(), error.getHttpStatus().toString()));
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<String> handleTooManyRequests(TooManyRequestsException e) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(e.getMessage());
    }
}
