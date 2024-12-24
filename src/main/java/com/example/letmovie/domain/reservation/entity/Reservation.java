package com.example.letmovie.domain.reservation.entity;

import com.example.letmovie.domain.member.entity.Member;
import com.example.letmovie.domain.movie.entity.Showtime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Reservation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "showTime_id")
    private Showtime showTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING) //enum
    private ReservationStatus status;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true) //양방향 매핑
    private List<ReservationSeat> reservationSeats = new ArrayList<>();

    private LocalDateTime reservationDate;

    private int totalSeats; // 총 좌석 수
    private int totalPrice; // 총 금액

    /**
     *  연관관계 메서드 - 양방향 설정
     */
    public void addReservationSeat(ReservationSeat reservationSeat){
        reservationSeats.add(reservationSeat);
        reservationSeat.setReservation(this);

    }

    /**
     *  생성 메서드
     */
    public static Reservation createReservation(Member member, Showtime showtime,List<ReservationSeat> reservationSeats){
        Reservation reservation = new Reservation();
        reservation.setShowTime(showtime);
        reservation.setMember(member);
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setReservationDate(LocalDateTime.now());
        reservation.setTotalSeats(reservationSeats.size());
        reservation.setTotalPrice(reservationSeats.stream().mapToInt(ReservationSeat::getSeatPrice).sum());

        for (ReservationSeat reservationSeat : reservationSeats) {
            reservation.addReservationSeat(reservationSeat); //양방향 설정
        }
        return reservation;
    }

    /**
     * 예매 취소 로직.
     */
    public void cancelReservation() {
        if(status.equals(ReservationStatus.VIEWED)){
            throw new IllegalStateException("상영완료 된 영화는 취소가 불가능합니다.");
        }

        this.status = ReservationStatus.CANCELLED; //예매 상태 변경

        for (ReservationSeat reservationSeat : reservationSeats) {
            reservationSeat.cancel(); //예매좌석->좌석에서 가능여부 true, 상영관 총 좌석 수 증가.
        }
    }
}
