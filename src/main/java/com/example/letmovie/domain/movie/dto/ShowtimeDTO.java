package com.example.letmovie.domain.movie.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ShowtimeDTO {
    private int movieId; // 영화 아이디
    private String movieName; // 영화 이름
    private String screenName; // 상영관 이름
    private int totalSeats; // 총 좌석
    private int remainingSeats; // 남은 좌석
    private String showDate; // 상영 날짜
    private String startTime; // 상영 시작 시간
}