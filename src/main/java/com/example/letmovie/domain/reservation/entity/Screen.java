package com.example.letmovie.domain.reservation.entity;

import com.example.letmovie.domain.movie.entity.Theater;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class Screen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "screen_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id")
    private Theater theater;

    private String screenName;
    private int totalSeats;
    private int remainingSeats;


}
