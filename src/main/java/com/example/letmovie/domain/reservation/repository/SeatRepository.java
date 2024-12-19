package com.example.letmovie.domain.reservation.repository;

import com.example.letmovie.domain.reservation.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat, Long> {
}
