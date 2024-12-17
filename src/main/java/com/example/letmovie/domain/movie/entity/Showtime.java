package com.example.letmovie.domain.movie.entity;

import com.example.letmovie.domain.reservation.entity.Screen;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@NoArgsConstructor
public class Showtime {

    @Id
    @Column(name = "showtime_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name="screen_id")
    private Screen screen;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @Column(nullable = false)
    private Date showtimeDate;
    @Column(nullable = false)
    private Date showtimeTime;
}