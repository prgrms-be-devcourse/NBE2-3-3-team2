package com.example.letmovie.domain.reservation.repository;

import com.example.letmovie.domain.reservation.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    @Query("SELECT s " +
            "FROM Seat s " +
            "WHERE s.seatCol = :seatCol AND s.seatLow = :seatLow AND s.screen.id = :screenId")
    Optional<Seat> findByColAndRowAndScreenId(@Param("seatCol") int seatCol,
                                   @Param("seatLow") int seatLow,
                                   @Param("screenId") Long screenId);
}
