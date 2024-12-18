package com.example.letmovie.domain.payment.dto.request;

import com.example.letmovie.domain.reservation.entity.ReservationStatus;

public class PaymentRequest {

    public record Info(
            Long reservation_id,
            Long member_id,
            String name,
            int totalPrice
    ) {

    }

    public record Approval(
            String cid,
            String tid,
            String pg_token,
            String partner_order_id,
            String partner_user_id
    ) {}

}
