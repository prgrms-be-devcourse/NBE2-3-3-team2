package com.example.letmovie.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCodes {


    // Payment
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND,  "결제 정보를 찾을 수 없습니다."),
    PAYMENT_ALREADY_CANCELLED(HttpStatus.BAD_REQUEST, "이미 취소된 결제입니다."),
    PAYMENT_FAILED(HttpStatus.BAD_REQUEST,  "결제 처리 중 오류가 발생했습니다."),
    INVALID_PAYMENT_STATUS(HttpStatus.BAD_REQUEST, "잘못된 결제 상태입니다.");

    private final HttpStatus httpStatus;
    private final String message;

    // Reservation



}
