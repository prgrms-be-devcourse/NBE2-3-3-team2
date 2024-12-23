package com.example.letmovie.domain.reservation.service;

import com.example.letmovie.domain.member.entity.Member;
import com.example.letmovie.domain.member.repository.MemberRepository;
import com.example.letmovie.domain.movie.entity.Showtime;
import com.example.letmovie.domain.reservation.dto.response.ReservationResponseDTO;
import com.example.letmovie.domain.reservation.entity.Reservation;
import com.example.letmovie.domain.reservation.entity.ReservationSeat;
import com.example.letmovie.domain.reservation.entity.Seat;
import com.example.letmovie.domain.reservation.repository.ReservationRepository;
import com.example.letmovie.domain.reservation.repository.SeatRepository;
import com.example.letmovie.domain.reservation.repository.ShowtimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;
    private final MemberRepository memberRepository;
    private final ShowtimeRepository showtimeRepository;


    @Transactional
    public ReservationResponseDTO reservation(List<String> seatList, Long memberId, Long showtimeId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new RuntimeException("회원이 없습니다.")); //전역 오류 핸들링으로 바꿔야 함.
        Showtime showtime = showtimeRepository.findById(showtimeId).orElseThrow(() -> new RuntimeException("영화 상영시간이 없습니다"));

        List<ReservationSeat> reservationSeats  = seatList.stream().map(seat -> {
            String[] split = seat.split("-");
            int row = Integer.parseInt(split[0]);
            int col = Integer.parseInt(split[1]);

            Long screenId = showtime.getScreen().getId();
            Seat seatEntity = seatRepository.findByColAndRowAndScreenId(col, row, screenId).orElseThrow(() -> new RuntimeException("좌석을 찾을 수 없습니다."));

            return ReservationSeat.createReservationSeat(seatEntity);
        }).collect(Collectors.toList());

        Reservation reservation = Reservation.createReservation(member, showtime, reservationSeats);
        reservationRepository.save(reservation);

        return new ReservationResponseDTO(
                reservation.getId(),
                memberId,
                member.getNickname(),
                reservation.getTotalPrice());
    }
}
