package com.example.letmovie.domain.payment.controller;

import com.example.letmovie.domain.payment.dto.request.PaymentRequest;
import com.example.letmovie.domain.payment.dto.response.PaymentResponse;
import com.example.letmovie.domain.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
@Tag(name = "KAKAO PAYMENT API")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/ready")
    public ResponseEntity<PaymentResponse.Ready> ready(@RequestBody PaymentRequest.Info request) {
        PaymentResponse.Ready response = paymentService.ready(request);
        return ResponseEntity.ok()
                .body(response);
    }

    @GetMapping("/success")
    public ResponseEntity<PaymentResponse.Success> success(@RequestParam("pg_token") String pg_token,
                                                           @RequestParam("tid") String tid,
                                                           @RequestParam("cid") String cid,
                                                           @RequestParam("partner_user_id")String partner_user_id,
                                                           @RequestParam("partner_order_id")String partner_order_id) {
        PaymentResponse.Success response = paymentService.success(pg_token, tid,cid,partner_user_id, partner_order_id);

        return ResponseEntity.ok()
                .body(response);
    }
}
