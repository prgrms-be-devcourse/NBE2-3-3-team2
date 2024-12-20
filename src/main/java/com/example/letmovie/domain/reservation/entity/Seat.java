package com.example.letmovie.domain.reservation.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Seat {

    @Id
    @Column(name ="seat_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id")
    private Screen screen;

    @OneToMany(mappedBy = "seat", cascade = CascadeType.ALL) //casecade
    private List<ReservationSeat> reservationSeats = new ArrayList<>();

    @Enumerated(EnumType.STRING) //enum
    private SeatType seatType;

    @Column(nullable = false)
    private int seatLow;

    @Column(nullable = false)
    private int seatCol;

    @Column(nullable = false)
    private boolean isAble;

    @Column(nullable = false)
    private int price;

    /**
     * 연관관계 메서드
     */
    public void setScreen(Screen screen) {
        this.screen = screen;
        screen.getSeats().add(this);
    }

    public void addReservationSeat(ReservationSeat reservationSeat) {
        reservationSeats.add(reservationSeat);
        reservationSeat.setSeat(this);
    }

    /**
     * 생성 메서드
     */




}
