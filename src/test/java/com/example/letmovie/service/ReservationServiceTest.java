package com.example.letmovie.service;

import com.example.letmovie.domain.member.entity.Authority;
import com.example.letmovie.domain.member.entity.Grade;
import com.example.letmovie.domain.member.entity.Member;
import com.example.letmovie.domain.member.entity.MemberStatus;
import com.example.letmovie.domain.member.repository.MemberRepository;
import com.example.letmovie.domain.movie.entity.Movie;
import com.example.letmovie.domain.movie.entity.Showtime;
import com.example.letmovie.domain.movie.entity.Status;
import com.example.letmovie.domain.movie.entity.Theater;
import com.example.letmovie.domain.movie.repository.MovieJpaRepository;
import com.example.letmovie.domain.reservation.dto.response.ReservationResponseDTO;
import com.example.letmovie.domain.reservation.entity.Screen;
import com.example.letmovie.domain.reservation.entity.Seat;
import com.example.letmovie.domain.reservation.entity.SeatType;
import com.example.letmovie.domain.reservation.repository.*;
import com.example.letmovie.domain.reservation.service.ReservationService;
import com.example.letmovie.domain.reservation.service.ShowtimeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
public class ReservationServiceTest {

    @Autowired
    private ShowtimeService showtimeService;

    @Autowired
    private ShowtimeRepository showtimeRepository;

    @Autowired
    private MovieJpaRepository movieJpaRepository;

    @Autowired
    private ScreenRepository screenRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private TheaterRepository theaterRepository;
    @Autowired
    private ReservationService reservationService;


    @AfterEach
    void clean(){
        reservationRepository.deleteAll(); // reservation 먼저 삭제
        seatRepository.deleteAll();
        showtimeRepository.deleteAll();
        screenRepository.deleteAll();
        theaterRepository.deleteAll();
        movieJpaRepository.deleteAll();
        memberRepository.deleteAll();

    }


    @Test
    @DisplayName("예약 생성 성공")
    void test1(){
        //given
        Theater theater = theaterRepository.save(Theater.builder()
                .theaterName("영국 롯데시네마")
                .build());

        Screen screen = screenRepository.save(Screen.builder()
                .theater(theater)
                .screenName("100관")
                .build());

        Movie movie = movieJpaRepository.save(new Movie(null,
                "오징어게임2", "M0001", "봉준호", "19세", "1200분",
                "2024-11-11", "액션", "쇼박스",
                Status.SHOW, "https://www.themoviedb.org/t/p/w1280/iIdBv0pMqZ9XKYOQeK42N1LZIeN.jpg", "still1.jpg", "줄거리 1", "100,000", "12340"));

        int rows = 10;
        int cols = 10;
        for (int row = 1; row <= rows; row++) {
            for (int col = 1; col <= cols; col++) {
                boolean isAble = !(row == 6 && col == 2); // 특정 좌석 비활성화
                int price = (row > 9) ? 20000 : 10000; // VIP와 REGULAR 가격 분리
                SeatType seatType = (row > 9) ? SeatType.VIP : SeatType.REGULAR; // VIP와 REGULAR 가격 분리
                Seat seat = Seat.builder()
                        .id(null)
                        .screen(screen)
                        .reservationSeats(new ArrayList<>())
                        .seatType(seatType)
                        .seatLow(row)
                        .seatCol(col)
                        .isAble(true)
                        .price(price).build();
                seatRepository.save(seat);
            }
        }

        Showtime showTime = showtimeRepository.save(Showtime.builder()
                .screen(screen)
                .movie(movie)
                .showtimeDate(LocalDate.parse("2024-12-28"))
                .showtimeTime(LocalTime.of(12, 0))
                .totalSeats(rows * cols)
                .remainingSeats(rows * cols)
                .build());

        Member member = Member.builder()
                .nickname("홍길동")
                .email("jinyoung@gmail.com")
                .password("1234")
                .birthDate("881213")
                .authority(Authority.ROLE_USER)
                .grade(Grade.GENERAL)
                .memberStatus(MemberStatus.AVAILABLE)
                .build();
        memberRepository.save(member);


        List<String> seats = new ArrayList<>();
        seats.add("1-1");
        seats.add("1-2");

        //when
        ReservationResponseDTO dto = reservationService.reservation(seats, member.getId(), showTime.getId());

        //then
        assertEquals(1,dto.getReservationId());
        assertEquals(1, dto.getMemberId());
        assertEquals("홍길동", dto.getMemberName());
        assertEquals(20000, dto.getTotalPrice());
    }

    @Test
    @DisplayName("특정 좌석 예약 생성 실패 테스트")
    void test2(){
        //given
        Theater theater = theaterRepository.save(Theater.builder()
                .theaterName("영국 롯데시네마")
                .build());

        Screen screen = screenRepository.save(Screen.builder()
                .theater(theater)
                .screenName("100관")
                .build());

        Movie movie = movieJpaRepository.save(new Movie(null,
                "오징어게임2", "M0001", "봉준호", "19세", "1200분",
                "2024-11-11", "액션", "쇼박스",
                Status.SHOW, "https://www.themoviedb.org/t/p/w1280/iIdBv0pMqZ9XKYOQeK42N1LZIeN.jpg", "still1.jpg", "줄거리 1", "100,000", "12340"));

        int rows = 10;
        int cols = 10;
        for (int row = 1; row <= rows; row++) {
            for (int col = 1; col <= cols; col++) {
                boolean isAble = !(row == 1 && col == 1); // 특정 좌석 비활성화
                int price = (row > 9) ? 20000 : 10000; // VIP와 REGULAR 가격 분리
                SeatType seatType = (row > 9) ? SeatType.VIP : SeatType.REGULAR; // VIP와 REGULAR 가격 분리
                Seat seat = Seat.builder()
                        .id(null)
                        .screen(screen)
                        .reservationSeats(new ArrayList<>())
                        .seatType(seatType)
                        .seatLow(row)
                        .seatCol(col)
                        .isAble(isAble)
                        .price(price).build();
                seatRepository.save(seat);
            }
        }

        Showtime showTime = showtimeRepository.save(Showtime.builder()
                .screen(screen)
                .movie(movie)
                .showtimeDate(LocalDate.parse("2024-12-28"))
                .showtimeTime(LocalTime.of(12, 0))
                .totalSeats(rows * cols)
                .remainingSeats(rows * cols)
                .build());

        Member member = Member.builder()
                .nickname("홍길동")
                .email("jinyoung@gmail.com")
                .password("1234")
                .birthDate("881213")
                .authority(Authority.ROLE_USER)
                .grade(Grade.GENERAL)
                .memberStatus(MemberStatus.AVAILABLE)
                .build();
        memberRepository.save(member);

        List<String> seats = new ArrayList<>();
        seats.add("1-1");

        //expected
        assertThrows(RuntimeException.class, () -> reservationService.reservation(seats, member.getId(), showTime.getId()));
    }

    @Test
    @DisplayName("vip 좌석 테스트")
    void test3(){
        //given
        Theater theater = theaterRepository.save(Theater.builder()
                .theaterName("영국 롯데시네마")
                .build());

        Screen screen = screenRepository.save(Screen.builder()
                .theater(theater)
                .screenName("100관")
                .build());

        Movie movie = movieJpaRepository.save(new Movie(null,
                "오징어게임2", "M0001", "봉준호", "19세", "1200분",
                "2024-11-11", "액션", "쇼박스",
                Status.SHOW, "https://www.themoviedb.org/t/p/w1280/iIdBv0pMqZ9XKYOQeK42N1LZIeN.jpg", "still1.jpg", "줄거리 1", "100,000", "12340"));

        int rows = 10;
        int cols = 10;
        for (int row = 1; row <= rows; row++) {
            for (int col = 1; col <= cols; col++) {
                boolean isAble = !(row == 1 && col == 1); // 특정 좌석 비활성화
                int price = (row > 9) ? 20000 : 10000; // VIP와 REGULAR 가격 분리
                SeatType seatType = (row > 9) ? SeatType.VIP : SeatType.REGULAR; // VIP와 REGULAR 가격 분리
                Seat seat = Seat.builder()
                        .id(null)
                        .screen(screen)
                        .reservationSeats(new ArrayList<>())
                        .seatType(seatType)
                        .seatLow(row)
                        .seatCol(col)
                        .isAble(isAble)
                        .price(price).build();
                seatRepository.save(seat);
            }
        }

        Showtime showTime = showtimeRepository.save(Showtime.builder()
                .screen(screen)
                .movie(movie)
                .showtimeDate(LocalDate.parse("2024-12-28"))
                .showtimeTime(LocalTime.of(12, 0))
                .totalSeats(rows * cols)
                .remainingSeats(rows * cols)
                .build());

        Member member = Member.builder()
                .nickname("홍길동")
                .email("jinyoung@gmail.com")
                .password("1234")
                .birthDate("881213")
                .authority(Authority.ROLE_USER)
                .grade(Grade.GENERAL)
                .memberStatus(MemberStatus.AVAILABLE)
                .build();
        memberRepository.save(member);

        List<String> seats = new ArrayList<>();
        seats.add("10-1");
        seats.add("10-2");

        //when
        ReservationResponseDTO dto = reservationService.reservation(seats, member.getId(), showTime.getId());

        //then
        assertEquals(40000, dto.getTotalPrice());
    }

    @Test
    @DisplayName("예약 생성시 예매 가능 좌석 수 확인")
    void test4(){
        //given
        Theater theater = theaterRepository.save(Theater.builder()
                .theaterName("영국 롯데시네마")
                .build());

        Screen screen = screenRepository.save(Screen.builder()
                .theater(theater)
                .screenName("100관")
                .build());

        Movie movie = movieJpaRepository.save(new Movie(null,
                "오징어게임2", "M0001", "봉준호", "19세", "1200분",
                "2024-11-11", "액션", "쇼박스",
                Status.SHOW, "https://www.themoviedb.org/t/p/w1280/iIdBv0pMqZ9XKYOQeK42N1LZIeN.jpg", "still1.jpg", "줄거리 1", "100,000", "12340"));

        int rows = 10;
        int cols = 10;
        for (int row = 1; row <= rows; row++) {
            for (int col = 1; col <= cols; col++) {
                boolean isAble = !(row == 1 && col == 1); // 특정 좌석 비활성화
                int price = (row > 9) ? 20000 : 10000; // VIP와 REGULAR 가격 분리
                SeatType seatType = (row > 9) ? SeatType.VIP : SeatType.REGULAR; // VIP와 REGULAR 가격 분리
                Seat seat = Seat.builder()
                        .id(null)
                        .screen(screen)
                        .reservationSeats(new ArrayList<>())
                        .seatType(seatType)
                        .seatLow(row)
                        .seatCol(col)
                        .isAble(true)
                        .price(price).build();
                seatRepository.save(seat);
            }
        }

        Showtime showTime = showtimeRepository.save(Showtime.builder()
                .screen(screen)
                .movie(movie)
                .showtimeDate(LocalDate.parse("2024-12-28"))
                .showtimeTime(LocalTime.of(12, 0))
                .totalSeats(rows * cols)
                .remainingSeats(rows * cols)
                .build());

        Member member = Member.builder()
                .nickname("홍길동")
                .email("jinyoung@gmail.com")
                .password("1234")
                .birthDate("881213")
                .authority(Authority.ROLE_USER)
                .grade(Grade.GENERAL)
                .memberStatus(MemberStatus.AVAILABLE)
                .build();
        memberRepository.save(member);

        List<String> seats = new ArrayList<>();
        seats.add("1-1");
        seats.add("1-2");

        //when
        reservationService.reservation(seats, member.getId(), showTime.getId());
        Showtime findShowTime = showtimeRepository.findById(showTime.getId()).orElseThrow();

        //then
        assertEquals(98,findShowTime.getRemainingSeats());
    }
}
