package com.example.letmovie.domain.reservation.service;

import com.example.letmovie.domain.movie.entity.Showtime;
import com.example.letmovie.domain.movie.entity.Theater;
import com.example.letmovie.domain.reservation.dto.response.MovieNamesResponseDTO;
import com.example.letmovie.domain.reservation.dto.response.ShowTimeResponseDTO;
import com.example.letmovie.domain.reservation.dto.response.TheaterResponseDTO;
import com.example.letmovie.domain.reservation.repository.ShowtimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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
    public MovieNamesResponseDTO findMovieNameByDate(String date) {
        return new MovieNamesResponseDTO(showtimeRepository.findDistinctMovieNamesByDate(LocalDate.parse(date)));
    }

    /**
     * 영화 이름, 날짜로 극장 가져오기.
     */
    public List<TheaterResponseDTO> findTheatersByMovieNameAndDate(String movieName, String date) {
        List<Theater> theaters = showtimeRepository.findTheatersByMovieNameAndShowtimeDate(movieName, LocalDate.parse(date));
        return theaters.stream()
                .map(theater -> new TheaterResponseDTO(theater.getId(), theater.getTheaterName()))
                .toList();
    }

    /**
     * 영화 날짜, 영화, 극장 선택으로 상영관,시간대 찾기.
     */
    public List<ShowTimeResponseDTO> findShowtimeByDateAndMovieNameAndTheater(String movieName, String date, String theaterName) {
        LocalDate showtimeDate = LocalDate.parse(date);
        LocalTime currentTime = LocalTime.now(); // 현재 시간

        List<Showtime> showtimes = showtimeRepository.findShowtimesByMovieAndDateAndTheater(theaterName, movieName, showtimeDate, currentTime);

        return showtimes.stream()
                .map(showtime -> new ShowTimeResponseDTO(
                        showtime.getScreen().getTheater().getTheaterName(),       // 극장이름
                        showtime.getScreen().getScreenName(),                    // 상영관 이름
                        String.valueOf(showtime.getScreen().getTotalSeats()),    // 상영관 전체 좌석
                        String.valueOf(showtime.getScreen().getRemainingSeats()), // 상영관 예약 가능 좌석
                        showtime.getShowtimeTime().toString(),                   // 상영 시작 시간
                        String.valueOf(showtime.getId())                         // 쇼타임 ID
                ))
                .toList();
    }

    public Optional<Showtime> findById(Long id) {
        return showtimeRepository.findById(id);
    }
}
