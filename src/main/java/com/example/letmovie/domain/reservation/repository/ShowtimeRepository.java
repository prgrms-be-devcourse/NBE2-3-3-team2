package com.example.letmovie.domain.reservation.repository;

import com.example.letmovie.domain.movie.entity.Showtime;
import com.example.letmovie.domain.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ShowtimeRepository  extends JpaRepository<Showtime, Long> {

    List<Showtime> findByShowtimeDate(LocalDate showtimeDate);
}
