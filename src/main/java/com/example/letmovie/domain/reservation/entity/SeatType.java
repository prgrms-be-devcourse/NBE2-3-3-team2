package com.example.letmovie.domain.reservation.entity;

import lombok.Getter;

@Getter
public enum SeatType {
    VIP(100000),         // VIP 좌석
    PREMIUM(50000),     // 프리미엄 좌석
    REGULAR(10000),     // 일반 좌석
    ECONOMY(7000);      // 저가 좌석

    private final int price;

    SeatType(int price) {
        if (price < 0) {
            throw new IllegalArgumentException("가격은 0원 이상이어야 합니다.");
        }
        this.price = price;
    }

}
