//package com.example.letmovie.domain.payment;
//
//import com.example.letmovie.domain.member.entity.Authority;
//import com.example.letmovie.domain.member.entity.Grade;
//import com.example.letmovie.domain.member.entity.Member;
//import com.example.letmovie.domain.member.entity.MemberStatus;
//import com.example.letmovie.domain.member.repository.MemberRepository;
//import com.example.letmovie.domain.movie.entity.Movie;
//import com.example.letmovie.domain.movie.entity.Showtime;
//import com.example.letmovie.domain.movie.entity.Status;
//import com.example.letmovie.domain.movie.entity.Theater;
//import com.example.letmovie.domain.movie.repository.MovieJpaRepository;
//import com.example.letmovie.domain.movie.repository.ShowtimeJpaRepository;
//import com.example.letmovie.domain.movie.repository.TheaterRepository;
//import com.example.letmovie.domain.payment.entity.Payment;
//import com.example.letmovie.domain.payment.entity.PaymentHistory;
//import com.example.letmovie.domain.payment.entity.PaymentStatus;
//import com.example.letmovie.domain.payment.repository.PaymentHistoryRepository;
//import com.example.letmovie.domain.payment.repository.PaymentRepository;
//import com.example.letmovie.domain.reservation.entity.*;
//import com.example.letmovie.domain.reservation.repository.ReservationRepository;
//import com.example.letmovie.domain.reservation.repository.ScreenRepository;
//import com.example.letmovie.domain.reservation.repository.SeatRepository;
//import jakarta.annotation.PostConstruct;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDateTime;
//import java.util.Arrays;
//
//@Component
//@RequiredArgsConstructor
//public class DataInitializer {
//    private final MemberRepository memberRepository;
//    private final MovieJpaRepository movieRepository;
//    private final ScreenRepository screenRepository;
//    private final SeatRepository seatRepository;
//    private final ShowtimeJpaRepository showtimeRepository;
//    private final ReservationRepository reservationRepository;
//    private final PaymentRepository paymentRepository;
//    private final PaymentHistoryRepository paymentHistoryRepository;
//    private final TheaterRepository theaterRepository;
//
//    @PostConstruct
//    public void init() {
//        // 1. Member 생성
//        Member member1 = Member.builder()
//                .email("user1@test.com")
//                .password("password123")
//                .nickname("user1")
//                .birthDate("19900101")
//                .authority(Authority.ROLE_USER)
//                .grade(Grade.GENERAL)
//                .memberStatus(MemberStatus.AVAILABLE)
//                .build();
//
//        Member member2 = Member.builder()
//                .email("admin@test.com")
//                .password("admin123")
//                .nickname("admin")
//                .birthDate("19800101")
//                .authority(Authority.ROLE_ADMIN)
//                .grade(Grade.VIP)
//                .memberStatus(MemberStatus.AVAILABLE)
//                .build();
//
//        Member member3 = Member.builder()
//                .email("user2@test.com")
//                .password("password456")
//                .nickname("user2")
//                .birthDate("19950101")
//                .authority(Authority.ROLE_USER)
//                .grade(Grade.GENERAL)
//                .memberStatus(MemberStatus.AVAILABLE)
//                .build();
//
//        memberRepository.saveAll(Arrays.asList(member1, member2, member3));
//
//        // 2. Movie 생성 // ALLARGS삭제했음 복구해야함.
//        Movie movie1 = Movie.builder()
//                .movieName("어벤져스")
//                .movieCode("M001")
//                .directorName("조스 웨던")
//                .rating("12세")
//                .runtime("142분")
//                .openDate("20220101")
//                .genreName("액션")
//                .companys("마블")
//                .status(Status.SHOW)
//                .posterImageUrl("poster1.jpg")
//                .stillImageUrl("still1.jpg")
//                .plot("히어로들의 이야기")
//                .salesAcc("1000000")
//                .audiAcc("500000")
//                .build();
//        movieRepository.save(movie1);
//
//        // 나머지 Movie 객체들도 비슷하게 생성...
//
//        // 3. Theater 생성
//        Theater theater1 = Theater.builder()
//                .theaterName("메가박스 강남")
//                .build();
//        theaterRepository.save(theater1);
//
//
//        // 4. Screen 생성
//        Screen screen1 = Screen.builder()
//                .theater(theater1)
//                .screenName("1관")
//                .totalSeats(100)
//                .remainingSeats(100)
//                .build();
//        screenRepository.save(screen1);
//        // 5. Seat 생성
//        Seat seat1 = Seat.builder()
//                .screen(screen1)
//                .seatType(SeatType.VIP)
//                .seatLow(1)
//                .seatCol(1)
//                .isAble(true)
//                .price(15000)
//                .build();
//        seatRepository.save(seat1);
//        // 6. Showtime 생성
//        Showtime showtime1 = Showtime.builder()
//                .screen(screen1)
//                .movie(movie1)
//                .showtimeDate("20240101")
//                .showtimeTime("1400")
//                .build();
//        showtimeRepository.save(showtime1);
//        // 7. Reservation 생성
//        Reservation reservation1 = Reservation.builder()
//                .member(member1)
//                .showTime(showtime1)
//                .status(ReservationStatus.PENDING)
//                .reservationDate(LocalDateTime.now())
//                .build();
//        reservationRepository.save(reservation1);
//        Reservation reservation2 = Reservation.builder()
//                .member(member1)
//                .showTime(showtime1)
//                .status(ReservationStatus.PENDING)
//                .reservationDate(LocalDateTime.now())
//                .build();
//        reservationRepository.save(reservation2);
//        Reservation reservation3 = Reservation.builder()
//                .member(member2)
//                .showTime(showtime1)
//                .status(ReservationStatus.PENDING)
//                .reservationDate(LocalDateTime.now())
//                .build();
//        reservationRepository.save(reservation3);
//
//        Reservation reservation4 = Reservation.builder()
//                .member(member2)
//                .showTime(showtime1)
//                .status(ReservationStatus.PENDING)
//                .reservationDate(LocalDateTime.now())
//                .build();
//        reservationRepository.save(reservation4);
//
//        Reservation reservation5 = Reservation.builder()
//                .member(member3)
//                .showTime(showtime1)
//                .status(ReservationStatus.PENDING)
//                .reservationDate(LocalDateTime.now())
//                .build();
//
//        reservationRepository.save(reservation5);
//
//        Reservation reservation6 = Reservation.builder()
//                .member(member3)
//                .showTime(showtime1)
//                .status(ReservationStatus.PENDING)
//                .reservationDate(LocalDateTime.now())
//                .build();
//        reservationRepository.save(reservation6);
//
//        Reservation reservation7 = Reservation.builder()
//                .member(member1)
//                .showTime(showtime1)
//                .status(ReservationStatus.PENDING)
//                .reservationDate(LocalDateTime.now())
//                .build();
//        reservationRepository.save(reservation7);
//
//        Reservation reservation8 = Reservation.builder()
//                .member(member1)
//                .showTime(showtime1)
//                .status(ReservationStatus.PENDING)
//                .reservationDate(LocalDateTime.now())
//                .build();
//        reservationRepository.save(reservation8);
//
//        Reservation reservation9 = Reservation.builder()
//                .member(member2)
//                .showTime(showtime1)
//                .status(ReservationStatus.PENDING)
//                .reservationDate(LocalDateTime.now())
//                .build();
//        reservationRepository.save(reservation9);
//
//        Reservation reservation10 = Reservation.builder()
//                .member(member3)
//                .showTime(showtime1)
//                .status(ReservationStatus.PENDING)
//                .reservationDate(LocalDateTime.now())
//                .build();
//        reservationRepository.save(reservation10);
//
//        Reservation reservation11 = Reservation.builder()
//                .member(member3)
//                .showTime(showtime1)
//                .status(ReservationStatus.PENDING)
//                .reservationDate(LocalDateTime.now())
//                .build();
//        reservationRepository.save(reservation11);
//
//        // 8. Payment 생성
//        Payment payment1 = Payment.builder()
//                .member(member1)
//                .reservation(reservation1)
//                .amount(15000)
//                .paymentStatus(PaymentStatus.PAYMENT_SUCCESS)
//                .build();
//        paymentRepository.save(payment1);
//
//        // Payment와 PaymentHistory 생성
//        Payment payment2 = Payment.builder()
//                .member(member1)
//                .reservation(reservation3)
//                .amount(15000)
//                .paymentStatus(PaymentStatus.PAYMENT_SUCCESS)
//                .build();
//        paymentRepository.save(payment2);
//
//        Payment payment3 = Payment.builder()
//                .member(member2)
//                .reservation(reservation4)
//                .amount(15000)
//                .paymentStatus(PaymentStatus.PAYMENT_SUCCESS)
//                .build();
//        paymentRepository.save(payment3);
//
//        Payment payment4 = Payment.builder()
//                .member(member2)
//                .reservation(reservation5)
//                .amount(15000)
//                .paymentStatus(PaymentStatus.PAYMENT_CANCELLED)
//                .build();
//        paymentRepository.save(payment4);
//
//        Payment payment5 = Payment.builder()
//                .member(member3)
//                .reservation(reservation6)
//                .amount(15000)
//                .paymentStatus(PaymentStatus.PAYMENT_SUCCESS)
//                .build();
//        paymentRepository.save(payment5);
//
//        Payment payment6 = Payment.builder()
//                .member(member3)
//                .reservation(reservation7)
//                .amount(15000)
//                .paymentStatus(PaymentStatus.PAYMENT_FAILED)
//                .build();
//        paymentRepository.save(payment6);
//
//        Payment payment7 = Payment.builder()
//                .member(member1)
//                .reservation(reservation8)
//                .amount(15000)
//                .paymentStatus(PaymentStatus.PAYMENT_SUCCESS)
//                .build();
//        paymentRepository.save(payment7);
//
//        Payment payment8 = Payment.builder()
//                .member(member1)
//                .reservation(reservation9)
//                .amount(15000)
//                .paymentStatus(PaymentStatus.PAYMENT_SUCCESS)
//                .build();
//        paymentRepository.save(payment8);
//
//        Payment payment9 = Payment.builder()
//                .member(member2)
//                .reservation(reservation10)
//                .amount(15000)
//                .paymentStatus(PaymentStatus.PAYMENT_CANCELLED)
//                .build();
//        paymentRepository.save(payment9);
//
//        // 9. PaymentHistory 생성
//        PaymentHistory paymentHistory1 = PaymentHistory.builder()
//                .payment(payment1)
//                .aid("aid_001")
//                .tid("tid_001")
//                .cid("TC0ONETIME")
//                .partnerOrderId("order_001")
//                .partnerUserId("user1")
//                .paymentMethodType("CARD")
//                .itemName("영화예매")
//                .quantity(1)
//                .amount(15000)
//                .createdAt(LocalDateTime.now())
//                .approvedAt(LocalDateTime.now())
//                .build();
//        paymentHistoryRepository.save(paymentHistory1);
//
//        // PaymentHistory 추가 생성
//        PaymentHistory paymentHistory2 = PaymentHistory.builder()
//                .payment(payment2)
//                .aid("aid_002")
//                .tid("tid_002")
//                .cid("TC0ONETIME")
//                .partnerOrderId("order_002")
//                .partnerUserId("user1")
//                .paymentMethodType("CARD")
//                .itemName("영화예매")
//                .quantity(1)
//                .amount(15000)
//                .createdAt(LocalDateTime.now())
//                .approvedAt(LocalDateTime.now())
//                .build();
//        paymentHistoryRepository.save(paymentHistory2);
//
//        PaymentHistory paymentHistory3 = PaymentHistory.builder()
//                .payment(payment3)
//                .aid("aid_003")
//                .tid("tid_003")
//                .cid("TC0ONETIME")
//                .partnerOrderId("order_003")
//                .partnerUserId("admin")
//                .paymentMethodType("KAKAO_PAY")
//                .itemName("영화예매")
//                .quantity(1)
//                .amount(15000)
//                .createdAt(LocalDateTime.now())
//                .approvedAt(LocalDateTime.now())
//                .build();
//        paymentHistoryRepository.save(paymentHistory3);
//
//        PaymentHistory paymentHistory4 = PaymentHistory.builder()
//                .payment(payment4)
//                .aid("aid_004")
//                .tid("tid_004")
//                .cid("TC0ONETIME")
//                .partnerOrderId("order_004")
//                .partnerUserId("admin")
//                .paymentMethodType("CARD")
//                .itemName("영화예매")
//                .quantity(1)
//                .amount(15000)
//                .createdAt(LocalDateTime.now())
//                .approvedAt(LocalDateTime.now())
//                .build();
//        paymentHistoryRepository.save(paymentHistory4);
//
//        PaymentHistory paymentHistory5 = PaymentHistory.builder()
//                .payment(payment5)
//                .aid("aid_005")
//                .tid("tid_005")
//                .cid("TC0ONETIME")
//                .partnerOrderId("order_005")
//                .partnerUserId("user2")
//                .paymentMethodType("NAVER_PAY")
//                .itemName("영화예매")
//                .quantity(1)
//                .amount(15000)
//                .createdAt(LocalDateTime.now())
//                .approvedAt(LocalDateTime.now())
//                .build();
//        paymentHistoryRepository.save(paymentHistory5);
//
//        PaymentHistory paymentHistory6 = PaymentHistory.builder()
//                .payment(payment6)
//                .aid("aid_006")
//                .tid("tid_006")
//                .cid("TC0ONETIME")
//                .partnerOrderId("order_006")
//                .partnerUserId("user2")
//                .paymentMethodType("KAKAO_PAY")
//                .itemName("영화예매")
//                .quantity(1)
//                .amount(15000)
//                .createdAt(LocalDateTime.now())
//                .approvedAt(LocalDateTime.now())
//                .build();
//        paymentHistoryRepository.save(paymentHistory6);
//
//        PaymentHistory paymentHistory7 = PaymentHistory.builder()
//                .payment(payment7)
//                .aid("aid_007")
//                .tid("tid_007")
//                .cid("TC0ONETIME")
//                .partnerOrderId("order_007")
//                .partnerUserId("user1")
//                .paymentMethodType("CARD")
//                .itemName("영화예매")
//                .quantity(1)
//                .amount(15000)
//                .createdAt(LocalDateTime.now())
//                .approvedAt(LocalDateTime.now())
//                .build();
//        paymentHistoryRepository.save(paymentHistory7);
//
//        PaymentHistory paymentHistory8 = PaymentHistory.builder()
//                .payment(payment8)
//                .aid("aid_008")
//                .tid("tid_008")
//                .cid("TC0ONETIME")
//                .partnerOrderId("order_008")
//                .partnerUserId("user1")
//                .paymentMethodType("NAVER_PAY")
//                .itemName("영화예매")
//                .quantity(1)
//                .amount(15000)
//                .createdAt(LocalDateTime.now())
//                .approvedAt(LocalDateTime.now())
//                .build();
//        paymentHistoryRepository.save(paymentHistory8);
//
//        PaymentHistory paymentHistory9 = PaymentHistory.builder()
//                .payment(payment9)
//                .aid("aid_009")
//                .tid("tid_009")
//                .cid("TC0ONETIME")
//                .partnerOrderId("order_009")
//                .partnerUserId("admin")
//                .paymentMethodType("CARD")
//                .itemName("영화예매")
//                .quantity(1)
//                .amount(15000)
//                .createdAt(LocalDateTime.now())
//                .approvedAt(LocalDateTime.now())
//                .build();
//        paymentHistoryRepository.save(paymentHistory9);
//
//        PaymentHistory paymentHistory10 = PaymentHistory.builder()
//                .payment(payment9)
//                .aid("aid_010")
//                .tid("tid_010")
//                .cid("TC0ONETIME")
//                .partnerOrderId("order_010")
//                .partnerUserId("user2")
//                .paymentMethodType("KAKAO_PAY")
//                .itemName("영화예매")
//                .quantity(1)
//                .amount(15000)
//                .createdAt(LocalDateTime.now())
//                .approvedAt(LocalDateTime.now())
//                .build();
//        paymentHistoryRepository.save(paymentHistory10);
//    }
//}
