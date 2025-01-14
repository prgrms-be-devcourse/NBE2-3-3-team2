package com.example.letmovie.global.exception;

import lombok.Getter;

@Getter
public class VerificationCodeException extends RuntimeException {

  private final ErrorCodes errorCode;

    public VerificationCodeException(ErrorCodes errorCode) {
      super(errorCode.getMessage());
      this.errorCode = errorCode;
    }
}
