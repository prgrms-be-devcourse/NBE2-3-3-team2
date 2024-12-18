package com.example.letmovie.domain.reservation.entity;

import com.example.letmovie.domain.member.entity.Member;
import com.example.letmovie.domain.movie.entity.Showtime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
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

    private LocalDateTime reservationDate;

}
