package com.example.letmovie.global.exception.controller;

import com.example.letmovie.global.exception.ErrorResponse;
import com.example.letmovie.global.exception.exceptionClass.LetMovieException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@ControllerAdvice
public class ReservationExceptionController {

    @ResponseBody
    @ExceptionHandler(LetMovieException.class)
    public ResponseEntity<ErrorResponse> LetMovieException(LetMovieException e){
        int statusCode = e.getStatusCode();

        ErrorResponse body = new ErrorResponse(String.valueOf(statusCode),e.getMessage());

        ResponseEntity<ErrorResponse> response = ResponseEntity.status(statusCode)
                .body(body);

        return response;
    }


//    @ExceptionHandler(LetMovieException.class)
//    public Object handleLetMovieException(LetMovieException e, HttpServletRequest request) {
//        int statusCode = e.getStatusCode();
//        String errorMessage = e.getMessage();
//
//        log.error("Exception occurred: {}", errorMessage);
//
//        // NOT_FOUND 상태 코드인 경우 Thymeleaf 화면으로 전환
//        if (statusCode == HttpStatus.NOT_FOUND.value()) {
//            ModelAndView modelAndView = new ModelAndView();
//            modelAndView.setViewName("error/not_found"); // Thymeleaf 템플릿 이름
//            modelAndView.addObject("errorCode", statusCode);
//            modelAndView.addObject("errorMessage", errorMessage);
//            return modelAndView;
//        }
//
//        // 그 외의 경우 JSON Response 반환
//        ErrorResponse body = new ErrorResponse(String.valueOf(statusCode), errorMessage);
//        return ResponseEntity.status(statusCode).body(body);
//    }
}
