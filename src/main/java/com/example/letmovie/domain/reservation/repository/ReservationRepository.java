package com.example.letmovie.domain.reservation.repository;

import com.example.letmovie.domain.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
