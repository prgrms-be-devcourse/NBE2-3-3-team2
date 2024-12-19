package com.example.letmovie.domain.movie.repository;

import com.example.letmovie.domain.movie.dto.ShowtimeDTO;
import com.example.letmovie.domain.movie.entity.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ShowtimeJpaRepository extends JpaRepository<Showtime, Long> {

    @Query("SELECT new com.example.letmovie.domain.movie.dto.ShowtimeDTO(" +
            "m.id, m.movieName, s.screenName, s.totalSeats, s.remainingSeats, " +
            "st.showtimeDate, " +
            "st.showtimeTime) " +
            "FROM Showtime st " +
            "JOIN st.movie m " +
            "JOIN st.screen s")
    List<ShowtimeDTO> findAllShowtime();
}
