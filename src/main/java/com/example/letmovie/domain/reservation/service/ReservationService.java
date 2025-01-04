package com.example.letmovie.domain.reservation.service;

import com.example.letmovie.domain.member.entity.Member;
import com.example.letmovie.domain.member.repository.MemberRepository;
import com.example.letmovie.domain.movie.entity.Showtime;
import com.example.letmovie.domain.payment.entity.Payment;
import com.example.letmovie.domain.payment.repository.PaymentRepository;
import com.example.letmovie.domain.payment.service.PaymentService;
import com.example.letmovie.domain.reservation.dto.response.ReservationResponseDTO;
import com.example.letmovie.domain.reservation.entity.Reservation;
import com.example.letmovie.domain.reservation.entity.ReservationSeat;
import com.example.letmovie.domain.reservation.entity.ReservationStatus;
import com.example.letmovie.domain.reservation.entity.Seat;
import com.example.letmovie.domain.reservation.repository.ReservationRepository;
import com.example.letmovie.domain.reservation.repository.SeatRepository;
import com.example.letmovie.domain.reservation.repository.ShowtimeRepository;
import com.example.letmovie.global.exception.ErrorCodes;
import com.example.letmovie.global.exception.PaymentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;
    private final MemberRepository memberRepository;
    private final ShowtimeRepository showtimeRepository;
    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;


    @Transactional
    public ReservationResponseDTO reservation(List<String> seatList, Long memberId, Long showtimeId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new RuntimeException("회원이 없습니다."));
        Showtime showtime = showtimeRepository.findByIdWithPessimisticLock(showtimeId).orElseThrow(() -> new RuntimeException("영화 상영시간이 없습니다"));

        // 먼저 Reservation 생성
        Reservation reservation = Reservation.builder()
                .showTime(showtime)
                .member(member)
                .reservationSeats(new ArrayList<>())
                .status(ReservationStatus.PENDING)
                .reservationDate(LocalDateTime.now())
                .totalSeats(seatList.size())
                .build();

        // Reservation을 먼저 저장
        reservationRepository.save(reservation);

        List<ReservationSeat> reservationSeats = seatList.stream().map(seat -> {
            String[] split = seat.split("-");
            int row = Integer.parseInt(split[0]);
            int col = Integer.parseInt(split[1]);

            Long screenId = showtime.getScreen().getId();
            Seat seatEntity = seatRepository.findByIdWithPessimisticLock(col, row, screenId)
                    .orElseThrow(() -> new RuntimeException("좌석을 찾을 수 없습니다."));

            if(!seatEntity.isAble()) {
                char rowLabel = (char) ('A' + row - 1);
                throw new RuntimeException("좌석 " + rowLabel  + "-" + col + "는 이미 선택된 좌석입니다.");
            }

            ReservationSeat reservationSeat = ReservationSeat.createReservationSeat(seatEntity, showtime);
            reservation.addReservationSeat(reservationSeat);

            return reservationSeat;
        }).collect(Collectors.toList());

        reservation.updateTotalPrice(reservationSeats.stream().mapToInt(ReservationSeat::getSeatPrice).sum());

        return new ReservationResponseDTO(
                reservation.getId(),
                memberId,
                member.getNickname(),
                reservation.getTotalPrice());
    }

    @Transactional
    public void reservationCancel(Long reservationId) {
            Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(() -> new RuntimeException("예매 번호가 없습니다."));
            Payment payment = paymentRepository.findByReservationId(reservationId).orElseThrow(() -> new PaymentException(ErrorCodes.PAYMENT_NOT_FOUND));
            log.info("예매취소시작");
            reservation.cancelReservation();
            log.info("예매취소시작2");

            paymentService.cancel(payment.getId());
            log.info("예매취소시작3");

    }
}
