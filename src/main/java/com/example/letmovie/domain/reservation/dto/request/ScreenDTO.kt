package com.example.letmovie.domain.reservation.dto.request

import jakarta.validation.constraints.NotNull

class ScreenDTO(
    var id: Long,  // 필수라면 Long, 선택 사항이면 Long? = null
    @field:NotNull(message = "Screen Name은 필수입니다.")
    var screenName: String, // "null 허용 안 함"이면 String, 허용이면 String?
    @field:NotNull(message = "Theater ID는 필수입니다.")
    var theaterId: Long
)
