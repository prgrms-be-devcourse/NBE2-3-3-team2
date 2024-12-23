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
