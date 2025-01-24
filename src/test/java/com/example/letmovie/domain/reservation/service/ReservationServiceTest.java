package com.example.letmovie.domain.reservation.service;

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
import com.example.letmovie.domain.reservation.facade.OptimisticLockReservationFacade;
import com.example.letmovie.domain.reservation.repository.*;
import com.example.letmovie.domain.reservation.service.lock.OptimisticLockReservationService;
import com.example.letmovie.domain.reservation.service.lock.PessimisticLockReservationService;
import com.example.letmovie.domain.reservation.service.ReservationService;
import com.example.letmovie.domain.reservation.service.ShowtimeService;
import com.example.letmovie.global.exception.exceptionClass.reservation.SeatNotFound;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
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

    //테스트를 위한 비관적 락 서비스 추가
    @Autowired
    private PessimisticLockReservationService pessimisticLockReservationService;

    @Autowired
    private OptimisticLockReservationFacade optimisticLockReservationService;

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
                .authority(Authority.USER)
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
                .authority(Authority.USER)
                .grade(Grade.GENERAL)
                .memberStatus(MemberStatus.AVAILABLE)
                .build();
        memberRepository.save(member);

        List<String> seats = new ArrayList<>();
        seats.add("1-1");

        //expected
        assertThrows(SeatNotFound.class, () -> reservationService.reservation(seats, member.getId(), showTime.getId()));
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
                .authority(Authority.USER)
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
                .authority(Authority.USER)
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


    @Test
    @DisplayName("동시성 이슈 생성 - 동시에 100개의 요청(예매)")
    void test5() throws InterruptedException {
        //given
        int threadCount = 100;
        //executorService는 비동기로 실행하는 작업을 단순화 하여 사용할 수 있게 도와주는 java의 api
        //32개의 작업을 동시에 실행할 수 있는 공간을 생성
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        //CountDownLatch는 다른 Thread에서 수행 중인 작업이 완료될때까지 대기할 수 있도록 도와주는 클래스이다.
        CountDownLatch latch = new CountDownLatch(threadCount);


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
                .authority(Authority.USER)
                .grade(Grade.GENERAL)
                .memberStatus(MemberStatus.AVAILABLE)
                .build();
        memberRepository.save(member);

        List<String> seats = new ArrayList<>();
        seats.add("1-1");

        //when & then
        assertThrows(AssertionFailedError.class, () -> {
            for (int i = 0; i < threadCount; i++) {
                executorService.submit(() -> {
                    try {
                        reservationService.reservation(seats, member.getId(), showTime.getId());
                    } finally {
                        latch.countDown();
                    }
                });
            }
            latch.await();

            Showtime findShowTime = showtimeRepository.findById(showTime.getId()).orElseThrow();
            assertEquals(0, findShowTime.getRemainingSeats()); // 이 부분에서 AssertionFailedError 발생
        });
        //레이스 컨디션 발생
    }

    @Test
    @DisplayName("동시성 이슈 비관적 락으로 해결")
    void test6() throws InterruptedException {
        //given
        int threadCount = 1000;
        //executorService는 비동기로 실행하는 작업을 단순화 하여 사용할 수 있게 도와주는 java의 api
        //32개의 작업을 동시에 실행할 수 있는 공간을 생성
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        //CountDownLatch는 다른 Thread에서 수행 중인 작업이 완료될때까지 대기할 수 있도록 도와주는 클래스이다.
        CountDownLatch latch = new CountDownLatch(threadCount);


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
                .authority(Authority.USER)
                .grade(Grade.GENERAL)
                .memberStatus(MemberStatus.AVAILABLE)
                .build();
        memberRepository.save(member);

        List<String> seats = new ArrayList<>();
        seats.add("1-1");

        //when
        for (int i = 0; i < threadCount; i++) {
            //동시에 100번 요청
            executorService.submit(() ->{
                try{
                    reservationService.reservation(seats, member.getId(), showTime.getId());
                }finally {
                    latch.countDown(); //요청이 하나 끝날 때마다 latch에서 1을 뺌
                }
            });
        }
        latch.await(); //비동기이기 때문에 latch가 0이 될 때까지 기다flsel

        Showtime findShowTime = showtimeRepository.findById(showTime.getId()).orElseThrow();
        //예상하는 수 100 - (1*100) = 0

        //then
        assertEquals(99,findShowTime.getRemainingSeats());
    }


    @Test
    @DisplayName("동시성 이슈 낙관적 락으로 해결")
    void test7() throws InterruptedException {
        //given
        int threadCount = 100;
        //executorService는 비동기로 실행하는 작업을 단순화 하여 사용할 수 있게 도와주는 java의 api
        //32개의 작업을 동시에 실행할 수 있는 공간을 생성
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        //CountDownLatch는 다른 Thread에서 수행 중인 작업이 완료될때까지 대기할 수 있도록 도와주는 클래스이다.
        CountDownLatch latch = new CountDownLatch(threadCount);


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
                .authority(Authority.USER)
                .grade(Grade.GENERAL)
                .memberStatus(MemberStatus.AVAILABLE)
                .build();
        memberRepository.save(member);

        List<String> seats = new ArrayList<>();
        seats.add("1-1");

        //when
        for (int i = 0; i < threadCount; i++) {
            //동시에 100번 요청
            executorService.submit(() ->{
                try{
                    optimisticLockReservationService.reservation(seats, member.getId(), showTime.getId());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown(); //요청이 하나 끝날 때마다 latch에서 1을 뺌
                }
            });
        }
        latch.await(); //비동기이기 때문에 latch가 0이 될 때까지 기다림

        Showtime findShowTime = showtimeRepository.findById(showTime.getId()).orElseThrow();
        //예상하는 수 100 - (1*100) = 0

        //then
        assertEquals(99,findShowTime.getRemainingSeats());
    }


    @Test
    @DisplayName("동시성 이슈 비관적 락으로 해결 + 응답시간 측정")
    void test6_withResponseTime() throws InterruptedException {
        //given
        int threadCount = 10000;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        Theater theater = theaterRepository.save(Theater.builder()
                .theaterName("영국 롯데시네마")
                .build());

        Screen screen = screenRepository.save(Screen.builder()
                .theater(theater)
                .screenName("100관")
                .build());

        Movie movie = movieJpaRepository.save(
                new Movie(null, "오징어게임2", "M0001", "봉준호", "19세", "1200분",
                        "2024-11-11", "액션", "쇼박스",
                        Status.SHOW, "url1", "still1.jpg", "줄거리 1", "100,000", "12340"));

        int rows = 10;
        int cols = 10;
        for (int row = 1; row <= rows; row++) {
            for (int col = 1; col <= cols; col++) {
                boolean isAble = !(row == 1 && col == 1);
                int price = (row > 9) ? 20000 : 10000;
                SeatType seatType = (row > 9) ? SeatType.VIP : SeatType.REGULAR;
                Seat seat = Seat.builder()
                        .screen(screen)
                        .seatType(seatType)
                        .seatLow(row)
                        .seatCol(col)
                        .isAble(true)
                        .price(price)
                        .build();
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
                .authority(Authority.USER)
                .grade(Grade.GENERAL)
                .memberStatus(MemberStatus.AVAILABLE)
                .build();
        memberRepository.save(member);

        List<String> seats = new ArrayList<>();
        seats.add("1-1");

        // 응답 시간을 기록할 리스트 (스레드 안전성을 위해 동기화 리스트 또는 ConcurrentLinkedQueue 사용)
        List<Long> responseTimes = Collections.synchronizedList(new ArrayList<>());
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        //when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                long startTime = System.nanoTime(); // 요청 시작 시간
                try {
                    reservationService.reservation(seats, member.getId(), showTime.getId());
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    long endTime = System.nanoTime(); // 요청 종료 시간
                    long duration = endTime - startTime; // 나노초 단위
                    responseTimes.add(duration);
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        Showtime findShowTime = showtimeRepository.findById(showTime.getId())
                .orElseThrow();
        // 예: 100명이 동시에 1개 좌석을 예약하면, 보통 1명 성공, 나머지는 실패
        // 여기서는 비관적 락으로 이미 하나만 선점되도록 처리된다고 가정했을 때 결과 = remainingSeats = 99
        assertEquals(99, findShowTime.getRemainingSeats());

        //then
        System.out.println("성공 요청 수: " + successCount.get());
        System.out.println("실패 요청 수: " + failCount.get());

        // 응답 시간 통계 (ms로 변환)
        List<Long> copy = new ArrayList<>(responseTimes);
        double minMs = copy.stream().mapToLong(v -> v).min().orElse(0L) / 1_000_000.0;
        double maxMs = copy.stream().mapToLong(v -> v).max().orElse(0L) / 1_000_000.0;
        double avgMs = copy.stream().mapToLong(v -> v).average().orElse(0.0) / 1_000_000.0;

        System.out.println("최소 응답 시간: " + String.format("%.2f", minMs) + " ms");
        System.out.println("최대 응답 시간: " + String.format("%.2f", maxMs) + " ms");
        System.out.println("평균 응답 시간: " + String.format("%.2f", avgMs) + " ms");
    }


    @Test
    @DisplayName("동시성 이슈 낙관적 락으로 해결 + 응답시간 측정")
    void test7_withResponseTime() throws InterruptedException {
        //given
        int threadCount = 10000;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        Theater theater = theaterRepository.save(Theater.builder()
                .theaterName("영국 롯데시네마")
                .build());

        Screen screen = screenRepository.save(Screen.builder()
                .theater(theater)
                .screenName("100관")
                .build());

        Movie movie = movieJpaRepository.save(
                new Movie(null, "오징어게임2", "M0001", "봉준호", "19세", "1200분",
                        "2024-11-11", "액션", "쇼박스",
                        Status.SHOW, "url1", "still1.jpg", "줄거리 1", "100,000", "12340"));

        int rows = 10;
        int cols = 10;
        for (int row = 1; row <= rows; row++) {
            for (int col = 1; col <= cols; col++) {
                boolean isAble = !(row == 1 && col == 1);
                int price = (row > 9) ? 20000 : 10000;
                SeatType seatType = (row > 9) ? SeatType.VIP : SeatType.REGULAR;
                Seat seat = Seat.builder()
                        .screen(screen)
                        .seatType(seatType)
                        .seatLow(row)
                        .seatCol(col)
                        .isAble(true)
                        .price(price)
                        .build();
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
                .authority(Authority.USER)
                .grade(Grade.GENERAL)
                .memberStatus(MemberStatus.AVAILABLE)
                .build();
        memberRepository.save(member);

        List<String> seats = new ArrayList<>();
        seats.add("1-1");

        // 응답 시간을 기록할 리스트 (스레드 안전성을 위해 동기화 리스트 또는 ConcurrentLinkedQueue 사용)
        List<Long> responseTimes = Collections.synchronizedList(new ArrayList<>());
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        //when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                long startTime = System.nanoTime(); // 요청 시작 시간
                try {
                    optimisticLockReservationService.reservation(seats, member.getId(), showTime.getId());
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    log.info("--------낙관적 락 에러메시지--------= {}", e.getMessage());
                    failCount.incrementAndGet();
                } finally {
                    long endTime = System.nanoTime(); // 요청 종료 시간
                    long duration = endTime - startTime; // 나노초 단위
                    responseTimes.add(duration);
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        Showtime findShowTime = showtimeRepository.findById(showTime.getId())
                .orElseThrow();
        // 예: 100명이 동시에 1개 좌석을 예약하면, 보통 1명 성공, 나머지는 실패
        // 여기서는 비관적 락으로 이미 하나만 선점되도록 처리된다고 가정했을 때 결과 = remainingSeats = 99
        assertEquals(99, findShowTime.getRemainingSeats());

        //then
        System.out.println("성공 요청 수: " + successCount.get());
        System.out.println("실패 요청 수: " + failCount.get());

        // 응답 시간 통계 (ms로 변환)
        List<Long> copy = new ArrayList<>(responseTimes);
        double minMs = copy.stream().mapToLong(v -> v).min().orElse(0L) / 1_000_000.0;
        double maxMs = copy.stream().mapToLong(v -> v).max().orElse(0L) / 1_000_000.0;
        double avgMs = copy.stream().mapToLong(v -> v).average().orElse(0.0) / 1_000_000.0;

        System.out.println("최소 응답 시간: " + String.format("%.2f", minMs) + " ms");
        System.out.println("최대 응답 시간: " + String.format("%.2f", maxMs) + " ms");
        System.out.println("평균 응답 시간: " + String.format("%.2f", avgMs) + " ms");
    }

    @Test
    @DisplayName("동시성 이슈 비관적 락 - 여러 좌석 vs 다수 유저")
    void test6_multiSeats() throws InterruptedException {
        // given
        int threadCount = 5000;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

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
                Status.SHOW, "url.jpg", "still1.jpg", "줄거리 1", "100,000", "12340"));

        int rows = 10;
        int cols = 10;
        for (int row = 1; row <= rows; row++) {
            for (int col = 1; col <= cols; col++) {
                Seat seat = Seat.builder()
                        .screen(screen)
                        .reservationSeats(new ArrayList<>())
                        .seatType(SeatType.REGULAR) // 예시로 전부 REGULAR
                        .seatLow(row)
                        .seatCol(col)
                        .isAble(true)
                        .price(10000)
                        .build();
                seatRepository.save(seat);
            }
        }

        // 좌석이 10x10 = 100개
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
                .authority(Authority.USER)
                .grade(Grade.GENERAL)
                .memberStatus(MemberStatus.AVAILABLE)
                .build();
        memberRepository.save(member);

        //응답 시간이나 성공/실패 카운트를 기록
        List<Long> responseTimes = Collections.synchronizedList(new ArrayList<>());
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                // 스레드마다 "랜덤 좌석"을 1개 선택
                // (row=1..10, col=1..10)
                Random rand = new Random();
                int randRow = rand.nextInt(rows) + 1;
                int randCol = rand.nextInt(cols) + 1;
                List<String> seats = new ArrayList<>();
                seats.add(randRow + "-" + randCol);

                long startTime = System.nanoTime();
                try {
                    // 비관적 락 예약 로직 호출
                    reservationService.reservation(seats, member.getId(), showTime.getId());
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    long endTime = System.nanoTime();
                    responseTimes.add(endTime - startTime);
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        Showtime findShowTime = showtimeRepository.findById(showTime.getId()).orElseThrow();

        // then
        // 1) 남은좌석 = 총좌석 - 실제로 성공한 예약 수
        //    (좌석 충돌 등으로 인해 일부만 성공했을 것)
        int actualSuccess = successCount.get();
        int expectedRemaining = (rows * cols) - actualSuccess;
        int dbRemainingSeats = findShowTime.getRemainingSeats();

        // 간단 검증
        System.out.println("성공 요청 수: " + actualSuccess);
        System.out.println("실패 요청 수: " + failCount.get());
        System.out.println("DB remainingSeats: " + dbRemainingSeats);

        List<Long> copyTimes = new ArrayList<>(responseTimes);
        double minMs = copyTimes.stream().mapToLong(v -> v).min().orElse(0) / 1_000_000.0;
        double maxMs = copyTimes.stream().mapToLong(v -> v).max().orElse(0) / 1_000_000.0;
        double avgMs = copyTimes.stream().mapToLong(v -> v).average().orElse(0) / 1_000_000.0;

        System.out.println("최소 응답 시간: " + String.format("%.2f", minMs) + " ms");
        System.out.println("최대 응답 시간: " + String.format("%.2f", maxMs) + " ms");
        System.out.println("평균 응답 시간: " + String.format("%.2f", avgMs) + " ms");

        // JUnit 단언(예시)
        // "실제 DB 남은좌석" == "예상 남은좌석" ?
        // 여러 스레드 충돌로 오차가 날 수도 있지만, 일반적으로 일치해야 합니다.
        assertEquals(expectedRemaining, dbRemainingSeats);
    }

    @Test
    @DisplayName("동시성 이슈 낙관적 락 - 여러 좌석 vs 다수 유저")
    void test7_multiSeats() throws InterruptedException {
        // given
        int threadCount = 5000;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

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
                Status.SHOW, "url.jpg", "still1.jpg", "줄거리 1", "100,000", "12340"));

        int rows = 10;
        int cols = 10;
        for (int row = 1; row <= rows; row++) {
            for (int col = 1; col <= cols; col++) {
                Seat seat = Seat.builder()
                        .screen(screen)
                        .reservationSeats(new ArrayList<>())
                        .seatType(SeatType.REGULAR) // 예시로 전부 REGULAR
                        .seatLow(row)
                        .seatCol(col)
                        .isAble(true)
                        .price(10000)
                        .build();
                seatRepository.save(seat);
            }
        }

        // 좌석이 10x10 = 100개
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
                .authority(Authority.USER)
                .grade(Grade.GENERAL)
                .memberStatus(MemberStatus.AVAILABLE)
                .build();
        memberRepository.save(member);

        //응답 시간이나 성공/실패 카운트를 기록
        List<Long> responseTimes = Collections.synchronizedList(new ArrayList<>());
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                // 스레드마다 "랜덤 좌석"을 1개 선택
                // (row=1..10, col=1..10)
                Random rand = new Random();
                int randRow = rand.nextInt(rows) + 1;
                int randCol = rand.nextInt(cols) + 1;
                List<String> seats = new ArrayList<>();
                seats.add(randRow + "-" + randCol);

                long startTime = System.nanoTime();
                try {
                    // 비관적 락 예약 로직 호출
                    optimisticLockReservationService.reservation(seats, member.getId(), showTime.getId());
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    long endTime = System.nanoTime();
                    responseTimes.add(endTime - startTime);
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        Showtime findShowTime = showtimeRepository.findById(showTime.getId()).orElseThrow();

        // then
        // 1) 남은좌석 = 총좌석 - 실제로 성공한 예약 수
        //    (좌석 충돌 등으로 인해 일부만 성공했을 것)
        int actualSuccess = successCount.get();
        int expectedRemaining = (rows * cols) - actualSuccess;
        int dbRemainingSeats = findShowTime.getRemainingSeats();

        // 간단 검증
        System.out.println("성공 요청 수: " + actualSuccess);
        System.out.println("실패 요청 수: " + failCount.get());
        System.out.println("DB remainingSeats: " + dbRemainingSeats);

        List<Long> copyTimes = new ArrayList<>(responseTimes);
        double minMs = copyTimes.stream().mapToLong(v -> v).min().orElse(0) / 1_000_000.0;
        double maxMs = copyTimes.stream().mapToLong(v -> v).max().orElse(0) / 1_000_000.0;
        double avgMs = copyTimes.stream().mapToLong(v -> v).average().orElse(0) / 1_000_000.0;

        System.out.println("최소 응답 시간: " + String.format("%.2f", minMs) + " ms");
        System.out.println("최대 응답 시간: " + String.format("%.2f", maxMs) + " ms");
        System.out.println("평균 응답 시간: " + String.format("%.2f", avgMs) + " ms");

        // JUnit 단언(예시)
        // "실제 DB 남은좌석" == "예상 남은좌석" ?
        // 여러 스레드 충돌로 오차가 날 수도 있지만, 일반적으로 일치해야 합니다.
        assertEquals(expectedRemaining, dbRemainingSeats);
    }

}
