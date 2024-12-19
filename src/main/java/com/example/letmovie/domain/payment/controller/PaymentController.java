package com.example.letmovie.domain.payment.controller;

import com.example.letmovie.domain.payment.dto.request.PaymentRequest;
import com.example.letmovie.domain.payment.dto.response.PaymentResponse;
import com.example.letmovie.domain.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
@Tag(name = "결제 진행 API")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/ready")
    @Operation(summary = "사용자 결제 준비 API 처리 ")
    public ResponseEntity<PaymentResponse.Ready> ready(@RequestBody PaymentRequest.Info request) {
        PaymentResponse.Ready response = paymentService.ready(request);
        return ResponseEntity.ok()
                .body(response);
    }

    @GetMapping("/success")
    @Operation(summary = "사용자 결제 성공 API 처리")
    public ResponseEntity<PaymentResponse.Success> success(@RequestParam("pg_token") String pg_token,
                                                           @RequestParam("tid") String tid,
                                                           @RequestParam("cid") String cid,
                                                           @RequestParam("partner_user_id") String partner_user_id,
                                                           @RequestParam("partner_order_id") String partner_order_id) {
        PaymentResponse.Success response = paymentService.success(pg_token, tid, cid, partner_user_id, partner_order_id);

        return ResponseEntity.ok()
                .body(response);
    }

//    @GetMapping("/fail")
//    @Operation(summary = "사용자 결제 실패시 대안책")
//    public Payment fail() {
//
//    }
}
