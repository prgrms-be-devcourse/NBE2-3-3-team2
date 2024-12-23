package com.example.letmovie.domain.reservation.entity;

import com.example.letmovie.domain.movie.entity.Theater;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Screen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "screen_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id")
    private Theater theater;

    @OneToMany(mappedBy = "screen", cascade = CascadeType.ALL) //양방향 세팅
    private List<Seat> seats = new ArrayList<>();

    @Column(nullable = false)
    private String screenName;

    @Column(nullable = false)
    private int totalSeats;

    @Column(nullable = false)
    private int remainingSeats;


}
