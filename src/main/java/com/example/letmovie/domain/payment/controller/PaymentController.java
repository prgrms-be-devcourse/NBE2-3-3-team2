package com.example.letmovie.domain.payment.controller;

import com.example.letmovie.domain.payment.dto.request.PaymentRequest;
import com.example.letmovie.domain.payment.dto.response.PaymentResponse;
import com.example.letmovie.domain.payment.service.PaymentHistoryService;
import com.example.letmovie.domain.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
@Tag(name = "결제 진행 API")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentHistoryService paymentHistoryService;

    @PostMapping("/ready")
    @Operation(summary = "사용자 결제 준비 API 처리 ")
    public ResponseEntity<PaymentResponse.Ready> paymentReady(@RequestBody PaymentRequest.Info request) {
        PaymentResponse.Ready response = paymentService.ready(request);
        return ResponseEntity.ok()
                .body(response);
    }

    @GetMapping("/success")
    @Operation(summary = "사용자 결제 성공 API 처리")
    public ResponseEntity<PaymentResponse.Success> paymentSuccess(@RequestParam("pg_token") String pg_token,
                                                                  @RequestParam("tid") String tid,
                                                                  @RequestParam("cid") String cid,
                                                                  @RequestParam("partner_user_id") String partner_user_id,
                                                                  @RequestParam("partner_order_id") String partner_order_id) {
        PaymentResponse.Success response = paymentService.success(pg_token, tid, cid, partner_user_id, partner_order_id);
        return ResponseEntity.ok()
                .body(response);
    }

    @PostMapping("/cancel")
    @Operation(summary = "사용자 결제 취소 API 처리")
    public ResponseEntity<PaymentResponse.Cancel> paymentCancel (@RequestParam("payment_id") Long paymentId) {
        PaymentResponse.Cancel response = paymentService.cancel(paymentId);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/mypayment")
    public ResponseEntity<List<PaymentResponse.Get>> getMemberPayments(@RequestParam(name = "member_id") Long memberId) {
        List<PaymentResponse.Get> response = paymentService.getMemberPayment(memberId);
        return ResponseEntity.ok()
                .body(response);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/{payment_id}")
    @Operation(summary = "관리자 결제 내역 삭제",description = "관리자 인증 권한 확인후, 결제 내역 삭제 가능")
    public ResponseEntity<Void> getMemberPayment(@PathVariable(name = "payment_id") Long paymentId) {
        paymentHistoryService.deletePaymentHistory(paymentId);
        return ResponseEntity.noContent().build();
    }




}
