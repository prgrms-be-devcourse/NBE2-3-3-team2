package com.example.letmovie.domain.reservation.entity;

import com.example.letmovie.domain.movie.entity.Showtime;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ReservationSeat {

    @NotNull
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_seat_id")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id")
    private Seat seat;

    @NotNull
    private int seatPrice; //좌석가격 추가

    /**
     *  연관관계 메서드
     */
    public void setSeat(Seat seat) {
        this.seat = seat;
        seat.getReservationSeats().add(this);
        seat.setAble(false);
    }

    public static ReservationSeat createReservationSeat(Seat seat, Showtime showtime) {
        ReservationSeat reservationSeat = new ReservationSeat();
        reservationSeat.setSeatPrice(seat.getPrice());
        reservationSeat.setSeat(seat); //seat에 대한 처리 ? -> seat 왜 양방향?

        //seat 감소 로직
        showtime.removeSeats(1);

        return reservationSeat;
    }

    public void cancel(Showtime showtime){
        seat.setAble(true);//예매 가능 여부 허용
        showtime.addSeats(1);
    }

}
