package com.example.letmovie.domain.reservation.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Seat {

    @Id
    @Column(name ="seat_id")
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id")
    private Screen screen;

    @NotNull
    @OneToMany(mappedBy = "seat", cascade = CascadeType.ALL) //casecade
    private List<ReservationSeat> reservationSeats = new ArrayList<>();

    @NotNull
    @Enumerated(EnumType.STRING) //enum
    private SeatType seatType;

    @NotNull
    private int seatLow;

    @NotNull
    private int seatCol;

    @NotNull
    private boolean isAble;

    @NotNull
    private int price;

    /**
     * 낙관적 락 테스트
     */
    @Version
    private int version;


    /**
     * 연관관계 메서드
     */
    public void setScreen(Screen screen) {
        this.screen = screen;
        screen.getSeats().add(this);
    }

    public void setAble(@NotNull boolean able) {
        isAble = able;
    }
}
