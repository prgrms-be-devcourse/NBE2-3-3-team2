package com.example.letmovie.domain.payment.service;

import com.example.letmovie.domain.payment.dto.request.PaymentRequest;
import com.example.letmovie.domain.payment.dto.response.PaymentResponse;
import com.example.letmovie.domain.payment.repository.PaymentHistoryRepository;
import com.example.letmovie.domain.payment.repository.PaymentRepository;
import com.example.letmovie.domain.payment.util.HttpRequestUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private static final String HOST = "https://open-api.kakaopay.com";
    private final HttpRequestUtil httpRequestUtil;
//    private final PaymentRepository paymentRepository;
//    private final PaymentHistoryRepository paymentHistoryRepository;
    @Value("${kakao.pay.secret.key}")
    private String secretKey;
    @Value("${kakao.pay.cid}")
    private String cid;

    public PaymentResponse.Ready ready(PaymentRequest.Info request) {
//        Payment payment = Payment.builder()
//                .member(memberRepository.findById(request.member_id())
//                        .orElseThrow(() -> new EntityNotFoundException("회원없음")))
//                .reservation(reservationRepository.findById(request.reservation_id())
//                        .orElseThrow.orElseThrow(() -> new EntityNotFoundException("회원없음")))
//                .amount(request.total_amount())
//                .paymentStatus(PaymentStatus.AWAITING_PAYMENT)
//                .build();


        HttpHeaders headers = httpRequestUtil.createHeaders(secretKey);
        Map<String, String> parameters = httpRequestUtil.createPaymentParams(request);
        return httpRequestUtil.post(
                HOST+"/online/v1/payment/ready",
                parameters,
                headers,
                PaymentResponse.Ready.class);
    }


    public PaymentResponse.Success success(String pgToken, String tid, String cid, String partnerUserId, String partnerOrderId) {
        HttpHeaders headers = httpRequestUtil.createHeaders(secretKey);
        Map<String, String> parameters = httpRequestUtil.createApprovalParams(pgToken, tid,partnerUserId, partnerOrderId,cid);
        PaymentResponse.Success response = httpRequestUtil.post(
                HOST + "/online/v1/payment/approve",
                parameters,
                headers,
                PaymentResponse.Success.class
        );
        return response;
//        /* payment 상태변경*/
//        Payment payment = paymentRepository.findByReservationId(
//                        Long.parseLong(partnerOrderId))
//                .orElseThrow(() -> new EntityNotFoundException("결제 정보를 찾을 수 없습니다."));
//        payment.updateStatus(PaymentStatus.PAYMENT_SUCCESS);
//
//        /* paymenthistory 내역 저장 */
//        paymentHistoryRepository.save(PaymentHistory.toPaymentHistory(payment, response));


    }
}
