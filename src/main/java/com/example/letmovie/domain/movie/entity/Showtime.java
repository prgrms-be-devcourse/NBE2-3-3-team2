package com.example.letmovie.domain.movie.entity;

import com.example.letmovie.domain.reservation.entity.Screen;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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

    @Column(nullable = false)
    private int totalSeats;

    @Column(nullable = false)
    private int remainingSeats;

    /**
     *  좌석 감소 로직.
     */
    public void removeSeats(int lose){
        int restSeats = remainingSeats-lose;
        if(restSeats < 0){
//            throw new NotEnoughStockException("need more stock", remainingSeats);
            throw new IllegalStateException("좌석이 부족합니다 남은좌석 :" + remainingSeats );
        }
        remainingSeats = restSeats;
    }

    /**
     * 취소시 남은 좌석 증가
     */
    public void addSeats(int add){
        if (this.remainingSeats + add > this.totalSeats) {
            throw new IllegalStateException("총 좌석 수를 초과할 수 없습니다.");
        }
        remainingSeats += add;
    }
}