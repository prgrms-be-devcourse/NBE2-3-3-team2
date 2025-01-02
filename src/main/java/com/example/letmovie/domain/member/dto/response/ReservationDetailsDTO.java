package com.example.letmovie.domain.member.dto.response;

import com.example.letmovie.domain.reservation.entity.ReservationStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ReservationDetailsDTO {
    private Long reservationId;                     // 예매 ID
    private ReservationStatus reservationStatus;    // 결제상태
    private String reservationStatusDisplayName;    // 결제상태 (한글 표시값)
    private String movieName;                       // 영화 이름
    private String posterUrl;                       // 포스터 이미지 URL
    private String theaterName;                     // 극장 이름
    private String screenName;                      // 관 이름
    private int totalSeats;                         // 예매한 총 좌석 수
    private LocalDateTime paidAt;                   // 결제일시
    private LocalDate showtimeDate;                 // 상영 날짜
    private LocalTime showtimeTime;                 // 상영 시간
    private String formattedPaymentAt;              // 변환된 결제일시
    @Setter
    private List<SeatDTO> seats;                    // 좌석


    public ReservationDetailsDTO(Long reservationId, ReservationStatus reservationStatus,
                                 String movieName, String posterUrl, String theaterName, String screenName, int totalSeats,
                                 LocalDateTime paidAt,LocalDate showtimeDate, LocalTime showtimeTime) {
        this.reservationId = reservationId;
        this.reservationStatus = reservationStatus;
        this.reservationStatusDisplayName = reservationStatus.getDisplayName();
        this.movieName = movieName;
        this.posterUrl = posterUrl;
        this.theaterName = theaterName;
        this.screenName = screenName;
        this.totalSeats = totalSeats;
        this.paidAt = paidAt;
        this.showtimeDate = showtimeDate;
        this.showtimeTime = showtimeTime;
        this.formattedPaymentAt = paidAt.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"));
        this.seats = new ArrayList<>();
    }

    // SeatDTO 리스트를 포함하는 생성자
    public ReservationDetailsDTO(Long reservationId, ReservationStatus reservationStatus,
                                 String movieName, String posterUrl, String theaterName, String screenName, int totalSeats,
                                 LocalDateTime paidAt,LocalDate showtimeDate, LocalTime showtimeTime,
                                 List<SeatDTO> seats) {
        this(reservationId, reservationStatus, movieName, posterUrl, theaterName, screenName, totalSeats,
                paidAt, showtimeDate, showtimeTime);
        this.reservationStatusDisplayName = reservationStatus.getDisplayName();
        this.formattedPaymentAt = paidAt.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"));
        this.seats = seats != null ? seats : new ArrayList<>(); // 초기화
    }

}
