package com.example.letmovie.domain.reservation.repository;

import com.example.letmovie.domain.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @EntityGraph(attributePaths = {
            "showTime",
            "showTime.movie",
            "showTime.screen",
            "showTime.screen.theater",
            "reservationSeats",
            "reservationSeats.seat"
    })
    @Query("SELECT r FROM Reservation r WHERE r.member.id = :memberId ORDER BY r.showTime.showtimeDate DESC, r.showTime.showtimeTime DESC")
    List<Reservation> findReservationsWithSeats(@Param("memberId") Long memberId);
}
