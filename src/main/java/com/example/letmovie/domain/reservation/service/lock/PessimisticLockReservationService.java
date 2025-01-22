package com.example.letmovie.domain.reservation.service.lock;

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
import com.example.letmovie.global.exception.exceptionClass.auth.MemberNotFoundException;
import com.example.letmovie.global.exception.exceptionClass.reservation.SeatNotFound;
import com.example.letmovie.global.exception.exceptionClass.reservation.ShowtimeNotFound;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 비관적 락 테스트를 위한 서비스 코드
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PessimisticLockReservationService {
    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;
    private final MemberRepository memberRepository;
    private final ShowtimeRepository showtimeRepository;

//락을 돌려받는 시점이 언제일까?

    @Transactional
    public ReservationResponseDTO reservation(List<String> seatList, Long memberId, Long showtimeId) {
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new); //전역 오류 핸들링으로 바꿔야 함.
//        Showtime showtime = showtimeRepository.findById(showtimeId).orElseThrow(() -> new RuntimeException("쇼타임이 없습니다."));
        Showtime showtime = showtimeRepository.findByIdWithPessimisticLock(showtimeId).orElseThrow(ShowtimeNotFound::new);

        List<ReservationSeat> reservationSeats = seatList.stream().map(seat -> {
            String[] split = seat.split("-");
            int row = Integer.parseInt(split[0]);
            int col = Integer.parseInt(split[1]);

            Long screenId = showtime.getScreen().getId();
            Seat seatEntity = seatRepository.findByIdWithPessimisticLock(col, row, screenId).orElseThrow(SeatNotFound::new);

            //테스트시 off
//            if (!seatEntity.isAble()) {
//                throw new RuntimeException("좌석 " + row + "-" + col + "은 예매가 불가능합니다.");
//            }

            return ReservationSeat.createReservationSeat(seatEntity, showtime);
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