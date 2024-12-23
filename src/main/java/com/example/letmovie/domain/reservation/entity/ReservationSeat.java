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

    private int seatPrice; //좌석가격 추가

    /**
     *  연관관계 메서드
     */
    public void setSeat(Seat seat) {
        this.seat = seat;
        seat.getReservationSeats().add(this);
        seat.setAble(false);
    }

    public static ReservationSeat createReservationSeat(Seat seat) {
        ReservationSeat reservationSeat = new ReservationSeat();
        reservationSeat.setSeatPrice(seat.getPrice());
        reservationSeat.setSeat(seat); //seat에 대한 처리 ? -> seat 왜 양방향?

        //seat 감소 로직
        Screen screen = seat.getScreen();
        screen.removeSeats(1);

        return reservationSeat;
    }
}
