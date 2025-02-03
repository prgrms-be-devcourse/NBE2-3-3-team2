package com.example.letmovie.domain.member.service;

import com.example.letmovie.domain.auth.util.SecurityUtil;
import com.example.letmovie.domain.member.dto.response.ReservationDetailsDTO;
import com.example.letmovie.domain.member.dto.response.SeatDTO;
import com.example.letmovie.domain.member.entity.Member;
import com.example.letmovie.domain.member.repository.MemberRepository;
import com.example.letmovie.domain.reservation.entity.Reservation;
import com.example.letmovie.domain.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final ReservationRepository reservationRepository;

    @Autowired
    public MyPageService(PasswordEncoder passwordEncoder, MemberRepository memberRepository, ReservationRepository reservationRepository) {
        this.passwordEncoder = passwordEncoder;
        this.memberRepository = memberRepository;
        SecurityUtil.setMemberRepository(memberRepository);  // memberRepository를 static 방식으로 설정
        this.reservationRepository = reservationRepository;
    }

    // 예매 목록 조회
    @Transactional(readOnly = true)
    public List<ReservationDetailsDTO> getReservationsWithSeats(Long memberId) {
        List<Reservation> reservations = reservationRepository.findReservationsWithSeats(memberId);

        return reservations.stream().map(reservation ->
                ReservationDetailsDTO.builder()
                        .reservationId(reservation.getId())
                        .reservationStatus(reservation.getStatus())
                        .reservationStatusDisplayName(reservation.getStatus().getDisplayName())
                        .movieName(reservation.getShowTime().getMovie().getMovieName())
                        .posterUrl(reservation.getShowTime().getMovie().getPosterImageUrl())
                        .theaterName(reservation.getShowTime().getScreen().getTheater().getTheaterName())
                        .screenName(reservation.getShowTime().getScreen().getScreenName())
                        .totalSeats(reservation.getTotalSeats())
                        .showtimeDate(reservation.getShowTime().getShowtimeDate())
                        .showtimeTime(reservation.getShowTime().getShowtimeTime())
                        .seats(reservation.getReservationSeats().stream()
                                .map(reservationSeat -> new SeatDTO(
                                        reservationSeat.getSeat().getId(),
                                        reservationSeat.getSeat().getSeatLow(),
                                        reservationSeat.getSeat().getSeatCol()))
                                .collect(Collectors.toList()))
                        .build()
        ).collect(Collectors.toList());
    }

    // 비밀번호 변경 및 저장
    public boolean changePassword(String currentPassword, String newPassword) {
        Member member = SecurityUtil.getCurrentMember()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 멤버입니다."));

        if (!passwordEncoder.matches(currentPassword, member.getPassword())) {
            return false;
        }

        String encodedPassword = passwordEncoder.encode(newPassword);
        member.updatePassword(encodedPassword);

        memberRepository.save(member);
        return true;
    }
}
