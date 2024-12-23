package com.example.letmovie.domain.reservation.service;

import com.example.letmovie.domain.reservation.entity.Reservation;
import com.example.letmovie.domain.reservation.entity.ReservationSeat;
import com.example.letmovie.domain.reservation.entity.Seat;
import com.example.letmovie.domain.reservation.repository.ReservationRepository;
import com.example.letmovie.domain.reservation.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;



//    @Transactional
//    public void reservation(List<String> seats, Long memberId) {
//        seats.forEach(seat -> {
//            String[] split = seat.split("-");
//            String col = split[0];
//            String row = split[1];
//
//            //seatRepository에서 행과 열로 id 찾기.
//            Seat seat1 = seatRepository.findByColRow();
//
//            ReservationSeat reservationSeat = ReservationSeat.createReservationSeat(seat1);
//
//        });
//
////        Reservation reservation = Reservation()
//
////        return reservation.getId();
//    }
}
