package com.example.letmovie.domain.movie.entity;

import com.example.letmovie.domain.reservation.entity.Screen;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Showtime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "showtime_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name="screen_id")
    private Screen screen;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @Column(nullable = false, columnDefinition = "Date")
    private LocalDate showtimeDate;
    @Column(nullable = false)
    private LocalTime showtimeTime;
}