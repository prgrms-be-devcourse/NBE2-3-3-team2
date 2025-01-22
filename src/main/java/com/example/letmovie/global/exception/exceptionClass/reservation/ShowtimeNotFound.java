package com.example.letmovie.global.exception.exceptionClass.reservation;

import com.example.letmovie.global.exception.ErrorCodes;
import com.example.letmovie.global.exception.exceptionClass.LetMovieException;

public class ShowtimeNotFound extends LetMovieException {

    private static final String MESSAGE = ErrorCodes.SHOWTIME_NOT_FOUND.getMessage(); // ErrorCodes에서 메시지 가져오기
    // 기본 메시지를 설정 -> 로그로 찍히는 부분
    public ShowtimeNotFound() {
        super(MESSAGE);
    }
    public ShowtimeNotFound(String message) {
        super(message);
    }

    //세부적인 오류 메시지 프론트엔드에 json형식으로 전달
    public ShowtimeNotFound(String fieldName, String message) {
        super(MESSAGE);
        addValidation(fieldName, message);
    }

    @Override
    public int getStatusCode() {
        return ErrorCodes.SHOWTIME_NOT_FOUND.getHttpStatus().value();
    }
}
