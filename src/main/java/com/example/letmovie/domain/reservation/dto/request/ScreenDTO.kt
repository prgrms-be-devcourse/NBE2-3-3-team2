package com.example.letmovie.domain.reservation.dto.request

import jakarta.validation.constraints.NotNull

data class ScreenDTO(
    val id: Long? = null,
    val screenName: String? = null,

    @field:NotNull(message = "Theater ID는 필수입니다.")
    val theaterId: Long?
)
