package com.example.letmovie.domain.reservation.entity;

import lombok.Getter;

@Getter
public enum ReservationStatus {

    PENDING("예매 중"),         // 예매 중
    CANCELLED("취소"),         // 취소
    COMPLETED("예매 완료"),     // 예매 완료
    VIEWED("상영 확인");        // 상영 확인 todo 상영시간 확인 후 지나면 알아서 바뀌게 해야 함.

    private final String displayName;

    ReservationStatus(String displayName) {
        this.displayName = displayName;
    }
}
