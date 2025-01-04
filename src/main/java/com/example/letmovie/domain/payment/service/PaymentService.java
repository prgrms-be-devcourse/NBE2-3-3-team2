package com.example.letmovie.domain.payment.service;

import com.example.letmovie.domain.member.entity.Member;
import com.example.letmovie.domain.member.repository.MemberRepository;
import com.example.letmovie.domain.payment.dto.request.PaymentRequest;
import com.example.letmovie.domain.payment.dto.response.PaymentResponse;
import com.example.letmovie.domain.payment.entity.Payment;
import com.example.letmovie.domain.payment.entity.PaymentHistory;
import com.example.letmovie.domain.payment.entity.PaymentStatus;
import com.example.letmovie.domain.payment.repository.PaymentHistoryRepository;
import com.example.letmovie.domain.payment.repository.PaymentRepository;
import com.example.letmovie.domain.payment.util.HttpRequestUtil;
import com.example.letmovie.domain.reservation.entity.Reservation;
import com.example.letmovie.domain.reservation.entity.ReservationStatus;
import com.example.letmovie.domain.reservation.repository.ReservationRepository;
import com.example.letmovie.global.exception.ErrorCodes;
import com.example.letmovie.global.exception.PaymentException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentService {

    private final HttpRequestUtil httpRequestUtil;
    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final PaymentFailureService paymentFailureService;

    @Value("${kakao.pay.host}")
    private String HOST;
    @Value("${kakao.pay.secret.key}")
    private String secretKey;
    @Value("${kakao.pay.cid}")
    private String cid;


    public PaymentResponse.Ready ready(PaymentRequest.Info request) {
        String movieName = initializePayment(request);
        HttpHeaders headers = httpRequestUtil.createHeaders(secretKey);
        Map<String, String> parameters = httpRequestUtil.createReadyParams(request,movieName);
        return httpRequestUtil.post(
                HOST+"/online/v1/payment/ready",
                parameters,
                headers,
                PaymentResponse.Ready.class);
    }

    public PaymentResponse.Success success(String pgToken, String tid, String cid, String partnerUserId, String partnerOrderId) {
        Long reservationId = Long.parseLong(partnerOrderId);
        log.info("변환된 reservationId = {}", reservationId);

        try{
        HttpHeaders headers = httpRequestUtil.createHeaders(secretKey);
        Map<String, String> parameters = httpRequestUtil.createApprovalParams(pgToken, tid,partnerUserId, partnerOrderId,cid);
        log.info("결제 승인 요청 파라미터 = {}", parameters);

        PaymentResponse.Success response = httpRequestUtil.post(
                HOST + "/online/v1/payment/approve",
                parameters,
                headers,
                PaymentResponse.Success.class
        );
        log.info("PG사 결제 승인 응답 = {}", response);
        initializePaymentAndPaymentHistory(reservationId, response);
        return response;
        } catch (Exception e){
            paymentFailureService.handlePaymentFailure(reservationId, e);
            throw new PaymentException(ErrorCodes.PAYMENT_FAILED);
        }
    }

    @Transactional
    public PaymentResponse.Cancel cancel(Long paymentId) {
        log.info("결제 취소 시작 - paymentId: {}", paymentId);
        PaymentHistory paymentHistory = paymentHistoryRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentException(ErrorCodes.PAYMENT_NOT_FOUND));
        try {
            HttpHeaders headers = httpRequestUtil.createHeaders(secretKey);
            Map<String, String> parameters = httpRequestUtil.createCancelParams(paymentHistory);
            log.info("카카오페이 결제 취소 요청 - parameters: {}, tid: {}, amount: {}");

            PaymentResponse.Cancel response = httpRequestUtil.post(
                    HOST + "/online/v1/payment/cancel",
                    parameters,
                    headers,
                    PaymentResponse.Cancel.class
            );
            log.info("카카오페이 결제 취소 응답 성공 = {}", response);
            // 결제 상태 업데이트
            Payment payment = paymentHistory.getPayment();
            payment.updateStatus(PaymentStatus.valueOf(response.status()));
            // 예매상태변경
            payment.getReservation().cancelReservation();
            // 취소 이력 생성 및 저장
            PaymentHistory cancelHistory = PaymentHistory.toCancelHistory(paymentHistory, response);
            paymentHistoryRepository.save(cancelHistory);

            return response;
        } catch (Exception e) {
            throw new PaymentException(ErrorCodes.PAYMENT_FAILED);
        }
    }


    @Transactional(readOnly = true)
    public List<PaymentResponse.Get> getMemberPayment(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new PaymentException(ErrorCodes.PAYMENT_NOT_FOUND));

        List<Payment> payments = paymentRepository.findByMemberId(memberId);
        return payments.stream()
                .map(PaymentResponse.Get::from)
                .collect(Collectors.toList());
    }

    private String initializePayment(PaymentRequest.Info request) {
        Payment payment = Payment.builder()
                .member(memberRepository.findById(request.member_id())
                        .orElseThrow(() -> new PaymentException(ErrorCodes.PAYMENT_NOT_FOUND)))
                .reservation(reservationRepository.findById(request.reservation_id())
                        .orElseThrow(() -> new PaymentException(ErrorCodes.PAYMENT_NOT_FOUND)))
                .amount(request.totalPrice())
                .paymentStatus(PaymentStatus.AWAITING_PAYMENT)
                .build();
        String name = payment.getReservation().getShowTime().getMovie().getMovieName();
        paymentRepository.save(payment);
        return name;
    }

    private void initializePaymentAndPaymentHistory(Long reservationId, PaymentResponse.Success response) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예약 없음."));
        log.info("상태 변경 전 reservation 정보 = {}", reservation.getStatus());
        reservation.setStatus(ReservationStatus.COMPLETED);
        log.info("상태 변경 후 reservation = {}", reservation.getStatus());

        Payment payment = paymentRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new PaymentException(ErrorCodes.PAYMENT_NOT_FOUND));

        log.info("상태 변경 전 payment 정보 = {}", payment.getPaymentStatus());
        payment.updateStatus(PaymentStatus.PAYMENT_SUCCESS);

        log.info("상태 변경 후 payment 정보 = {}", payment.getPaymentStatus());
        PaymentHistory paymentHistory = PaymentHistory.toPaymentHistory(payment, response);
        log.info("생성된 결제 이력 = {}", paymentHistory.getPayment().getId());
        log.info("생성된 결제 이력 = {}", paymentHistory.getPayment().getPaymentStatus());

        payment.updatePaymentMethodType(paymentHistory.getPaymentMethodType());
        paymentHistoryRepository.save(paymentHistory);

    }


}
