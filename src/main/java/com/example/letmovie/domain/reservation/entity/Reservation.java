package com.example.letmovie.domain.reservation.entity;

import com.example.letmovie.domain.member.entity.Member;
import com.example.letmovie.domain.movie.entity.Showtime;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Reservation {

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Reservation_id")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "showTime_id")
    private Showtime showTime;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING) //enum
    private ReservationStatus status;

    @NotNull
    @Builder.Default // Builder를 사용할 때 기본값 설정
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true) //양방향 매핑
    private List<ReservationSeat> reservationSeats = new ArrayList<>();

    @NotNull
    private LocalDateTime reservationDate;

    @NotNull
    private int totalSeats; // 총 좌석 수

    @NotNull
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
        Reservation reservation = Reservation.builder()
                .showTime(showtime)
                .member(member)
                .status(ReservationStatus.PENDING)
                .reservationDate(LocalDateTime.now())
                .totalSeats(reservationSeats.size())
                .totalPrice(reservationSeats.stream().mapToInt(ReservationSeat::getSeatPrice).sum())
                .build();


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
            reservationSeat.cancel(this.getShowTime()); //예매좌석->좌석에서 가능여부 true, 상영관 총 좌석 수 증가.
        }
    }
}
