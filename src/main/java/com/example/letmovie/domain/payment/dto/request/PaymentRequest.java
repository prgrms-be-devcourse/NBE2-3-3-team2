package com.example.letmovie.domain.payment.dto.request;

import com.example.letmovie.domain.reservation.entity.ReservationStatus;

public class PaymentRequest {

    /* 예매정보에서 받아올 값들.*/
    public record Info(
            Long reservation_id,
            Long member_id,
            String name,
            int totalPrice
    ) {

    }


}
