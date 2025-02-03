package com.example.letmovie.domain.reservation.dto.request


class ReserveSeatsRequestDTO {
    val seats: List<String>? = null // 좌석 목록
    val showtimeId: Long? = null // 쇼타임 ID
}