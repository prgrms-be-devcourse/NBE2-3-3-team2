package com.example.letmovie.domain.reservation.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ReservationSeat {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_seat_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id")
    private Seat seat;


    public static ReservationSeat createReservationSeat(Seat seat) {
        ReservationSeat reservationSeat = new ReservationSeat();
        reservationSeat.setSeat(seat);

        return reservationSeat;
    }





}
