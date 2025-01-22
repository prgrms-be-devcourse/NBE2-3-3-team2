package com.example.letmovie.global.exception.exceptionClass.reservation;

import com.example.letmovie.global.exception.ErrorCodes;
import com.example.letmovie.global.exception.exceptionClass.LetMovieException;

public class ReservationNotFound extends LetMovieException {

    private static final String MESSAGE = ErrorCodes.RESERVATION_NOT_FOUND.getMessage();

    public ReservationNotFound() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return ErrorCodes.RESERVATION_NOT_FOUND.getHttpStatus().value();
    }
}
