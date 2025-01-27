package com.example.letmovie.domain.movie.entity;

import com.example.letmovie.domain.reservation.entity.Screen;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(
        name = "showtime",
        indexes = {
                @Index(name = "idx_movie_theater_date", columnList = "showtimeDate")
        }
)
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Showtime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "showtime_id")
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name="screen_id")
    private Screen screen;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @NotNull
    @Column(columnDefinition = "Date")
    private LocalDate showtimeDate;

    @NotNull
    private LocalTime showtimeTime;

    @NotNull
    private int totalSeats;

    @NotNull
    private int remainingSeats;

    /**
     * 낙관적 락 테스트
     */
    @Version
    private int version;

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