package com.example.letmovie.global.exception.exceptionClass.payment;

import com.example.letmovie.global.exception.ErrorCodes;
import lombok.Getter;

@Getter
public class VerificationCodeException extends RuntimeException {

  private final ErrorCodes errorCode;

    public VerificationCodeException(ErrorCodes errorCode) {
      super(errorCode.getMessage());
      this.errorCode = errorCode;
    }
}
