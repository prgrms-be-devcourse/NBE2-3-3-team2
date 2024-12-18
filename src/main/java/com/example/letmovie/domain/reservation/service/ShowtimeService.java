package com.example.letmovie.domain.reservation.service;

import com.example.letmovie.domain.movie.entity.Movie;
import com.example.letmovie.domain.movie.entity.Showtime;
import com.example.letmovie.domain.reservation.repository.ShowtimeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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



}
