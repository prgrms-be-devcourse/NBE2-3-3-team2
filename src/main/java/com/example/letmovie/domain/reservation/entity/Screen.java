package com.example.letmovie.domain.reservation.entity;

import com.example.letmovie.domain.movie.entity.Theater;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Screen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "screen_id")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id")
    private Theater theater;

    @NotNull
    @Builder.Default
    @OneToMany(mappedBy = "screen", cascade = CascadeType.ALL) //양방향 세팅
    private List<Seat> seats = new ArrayList<>();

    @NotNull
    private String screenName;

    public void setId(@NotNull Long id) {
        this.id = id;
    }

    public void setTheater(@NotNull Theater theater) {
        this.theater = theater;
    }

    public void setScreenName(@NotNull String screenName) {
        this.screenName = screenName;
    }
}
