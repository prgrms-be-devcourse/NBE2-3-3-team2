package com.example.letmovie.domain.reservation.facade;

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
import com.example.letmovie.domain.reservation.entity.Screen;
import com.example.letmovie.domain.reservation.entity.Seat;
import com.example.letmovie.domain.reservation.entity.SeatType;
import com.example.letmovie.domain.reservation.repository.*;
import com.example.letmovie.domain.reservation.service.ReservationService;
import com.example.letmovie.domain.reservation.service.ShowtimeService;
import com.example.letmovie.domain.reservation.service.lock.PessimisticLockReservationService;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class OptimisticLockReservationFacadeTest {

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

    //테스트를 위한 낙관적 락 서비스 추가
    @Autowired
    private OptimisticLockReservationFacade optimisticLockReservationFacade;


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
    @DisplayName("동시성 이슈 낙관적 락으로 해결")
    void test1() throws InterruptedException {
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
                .authority(Authority.ROLE_USER)
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
                    optimisticLockReservationFacade.reservation(seats, member.getId(), showTime.getId());
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
        assertEquals(0,findShowTime.getRemainingSeats());
    }
}