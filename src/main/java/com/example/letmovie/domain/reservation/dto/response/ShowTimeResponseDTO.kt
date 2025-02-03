package com.example.letmovie.domain.reservation.dto.response

class ShowTimeResponseDTO (
    var theaterName: String? = null, // 극장이름
    var screenName: String? = null,// 상영관 이름
    var screenTotalSeat: Int = 0, // 상영관 전체 좌석
    var screenRemainSeat: Int = 0, // 상영관 예약 가능 좌석
    var showtime: String? = null, // 상영 시작 시간
    var showtimeId: Long? = null // 쇼타임 ID
)