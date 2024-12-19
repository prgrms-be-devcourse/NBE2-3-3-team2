package com.example.letmovie.domain.reservation.service;

import com.example.letmovie.domain.movie.entity.Movie;
import com.example.letmovie.domain.movie.entity.Showtime;
import com.example.letmovie.domain.movie.entity.Theater;
import com.example.letmovie.domain.reservation.repository.ShowtimeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ShowtimeService {

    private final ShowtimeRepository showtimeRepository;


    /**
     * 날짜로 showtime 객체 가져오기.
     */
    public List<Showtime> findShowTimesByDate(String date) {
        return showtimeRepository.findByShowtimeDate(LocalDate.parse(date));
    }


    /**
     *  날짜로 영화 이름 가져오기.
     */
    public List<String> findMovieNameByDate(String date) {
        List<Showtime> showtimes = showtimeRepository.findByShowtimeDate(LocalDate.parse(date));

        List<String> movieNames = new ArrayList<>();

        for (Showtime showtime : showtimes) {
            Movie movie = showtime.getMovie();
            String movieName = movie.getMovieName();
            movieNames.add(movieName);
        }

        return movieNames;
    }

    /**
     * 영화 이름, 날짜로 극장 가져오기.
     */
    public List<Map<String, String>> findTheatersByMovieNameAndDate(String movieName, String date) {

        //영화이름으로 극장 가져오기.. 상영일도 맞아야 함!
        //1. 영화 상영시간으로 쇼타임 객체 가져오기.
        //2. 쇼타임 아이디 값으로 극장 찾아오기.
        List<Theater> theaters = showtimeRepository.findTheatersByMovieNameAndShowtimeDate(movieName, LocalDate.parse(date));


        // 극장 데이터를 가공 -> { id : 1, name : 용산 메가박스}
        return theaters.stream()
                .map(theater -> Map.of(
                        "id", String.valueOf(theater.getId()),
                        "name", theater.getTheaterName()
                ))
                .toList();
    }

    /**
     * 영화 날짜, 영화, 극장 선택으로 상영관,시간대 찾기.
     */
    public List<Map<String, String>> findShowtimeByDateAndMovieNameAndTheater(String movieName, String date, String theaterName) {
        LocalDate showtimeDate = LocalDate.parse(date);
        LocalTime currentTime = LocalTime.now(); // 현재 시간

        List<Showtime> showtimes = showtimeRepository.findShowtimesByMovieAndDateAndTheater(theaterName, movieName, showtimeDate, currentTime);


        // 데이터 가공
        return showtimes.stream()
                .map(showtime -> Map.of(
                        "theaterName", showtime.getScreen().getTheater().getTheaterName(), //극장이름
                        "screenName", showtime.getScreen().getScreenName(), //상영관 이름
                        "screenTotalSeat", String.valueOf(showtime.getScreen().getTotalSeats()), //상영관 전체 좌석
                        "screenRemainSeat", String.valueOf(showtime.getScreen().getRemainingSeats()), //상영관 예약 가능 좌석
                        "showtime", showtime.getShowtimeTime().toString(), //상영 시작 시간
                        "showtimeId", String.valueOf(showtime.getId())
                ))
                .toList();
    }

    public Optional<Showtime> findById(Long id) {
        return showtimeRepository.findById(id);
    }
}
