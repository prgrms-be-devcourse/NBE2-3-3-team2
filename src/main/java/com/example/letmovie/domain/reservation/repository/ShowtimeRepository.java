package com.example.letmovie.domain.reservation.repository;

import com.example.letmovie.domain.movie.entity.Showtime;
import com.example.letmovie.domain.movie.entity.Theater;
import com.example.letmovie.domain.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ShowtimeRepository  extends JpaRepository<Showtime, Long> {

    List<Showtime> findByShowtimeDate(LocalDate showtimeDate);

    @Query("SELECT DISTINCT t FROM Showtime s " +
            "JOIN s.screen sc " +
            "JOIN sc.theater t " +
            "WHERE s.movie.movieName = :movieName AND s.showtimeDate = :showtimeDate")
    List<Theater> findTheatersByMovieNameAndShowtimeDate(
            @Param("movieName") String movieName,
            @Param("showtimeDate") LocalDate showtimeDate);


}
