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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShowtimeService {

    private final ShowtimeRepository showtimeRepository;


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
        boolean isToday = showtimeDate.equals(LocalDate.now());

        List<Showtime> showtimes = showtimeRepository.findShowtimesByMovieAndDateAndTheater(theaterName, movieName, showtimeDate, isToday);

        return showtimes.stream()
                .map(showtime -> new ShowTimeResponseDTO(
                        showtime.getScreen().getTheater().getTheaterName(),
                        showtime.getScreen().getScreenName(),
                        showtime.getTotalSeats(),
                        showtime.getRemainingSeats(),
                        showtime.getShowtimeTime().toString(),
                        showtime.getId()
                ))
                .toList();
    }

    public Optional<Showtime> findById(Long id) {
        return showtimeRepository.findById(id);
    }
}
