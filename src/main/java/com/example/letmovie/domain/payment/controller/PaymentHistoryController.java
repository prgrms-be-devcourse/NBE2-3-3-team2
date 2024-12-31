package com.example.letmovie.domain.payment.controller;

import com.example.letmovie.domain.payment.dto.response.PaymentHistoryResponse;
import com.example.letmovie.domain.payment.entity.PaymentHistory;
import com.example.letmovie.domain.payment.service.PaymentHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/paymentHistory")
@RequiredArgsConstructor
@Tag(name = " 결제 내역 API")
public class PaymentHistoryController {

    private final PaymentHistoryService paymentHistoryService;

//    @GetMapping("/{payment_id}")
//    @Operation(summary = "결제 상세 조회")
//    public ResponseEntity<PaymentHistoryResponse.Info> getMemberPaymentHistory(@PathVariable("payment_id") Long paymentId) {
//        return ResponseEntity.ok()
//                .body(paymentHistoryService.getPaymentHistoryDetail(paymentId));
//    }

//    @GetMapping("/payment_histories")


//    @GetMapping("/search")

}
