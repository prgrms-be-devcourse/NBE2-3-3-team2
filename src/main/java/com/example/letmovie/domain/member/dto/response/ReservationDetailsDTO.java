package com.example.letmovie.domain.member.dto.response;

import com.example.letmovie.domain.reservation.entity.ReservationStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ReservationDetailsDTO {
    private Long reservationId;                     // 예매 ID
    private ReservationStatus reservationStatus;    // 예매상태
    private String reservationStatusDisplayName;    // 예매상태 (한글 표시값)
    private String movieName;                       // 영화 이름
    private String posterUrl;                       // 포스터 이미지 URL
    private String theaterName;                     // 극장 이름
    private String screenName;                      // 관 이름
    private int totalSeats;                         // 예매한 총 좌석 수
    private LocalDate showtimeDate;                 // 상영 날짜
    private LocalTime showtimeTime;                 // 상영 시간
    @Setter
    private List<SeatDTO> seats;                    // 좌석
}
