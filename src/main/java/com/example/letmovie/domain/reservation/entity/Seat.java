package com.example.letmovie.domain.reservation.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Seat {

    @Id
    @Column(name ="seat_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id")
    private Screen screen;

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

}
