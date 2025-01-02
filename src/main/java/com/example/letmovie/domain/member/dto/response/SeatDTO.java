package com.example.letmovie.domain.member.dto.response;

import lombok.Getter;

@Getter
public class SeatDTO {
    private Long id;
    private int seatLow;
    private int seatCol;

    public SeatDTO(Long id, int seatLow, int seatCol) {
        this.id = id;
        this.seatLow = seatLow;
        this.seatCol = seatCol;
    }
}
