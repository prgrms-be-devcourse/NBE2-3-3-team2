package com.example.letmovie.domain.payment.service;

import com.example.letmovie.domain.payment.dto.response.PaymentResponse;
import com.example.letmovie.domain.payment.entity.Payment;
import com.example.letmovie.domain.payment.entity.PaymentHistory;
import com.example.letmovie.domain.payment.entity.PaymentStatus;
import com.example.letmovie.domain.payment.repository.PaymentHistoryRepository;
import com.example.letmovie.domain.payment.repository.PaymentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentFailureService {
    
    private final PaymentRepository paymentRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handlePaymentFailure(Long reservationId, Exception e) {
        log.error("결제 실패 - reservationId: {}, error: {}", reservationId, e.getMessage());

        try {
            Payment payment = paymentRepository.findByReservationId(reservationId)
                    .orElseThrow(() -> new EntityNotFoundException("결제 정보를 찾을 수 없습니다."));

            payment.updateStatus(PaymentStatus.PAYMENT_FAILED);
            Payment savedPayment = paymentRepository.save(payment);

            // 에러 응답 파싱
            String errorCode = "";
            String errorMessage = "";

            if (e instanceof HttpClientErrorException) {
                String errorResponse = ((HttpClientErrorException) e).getResponseBodyAsString();
                try {
                    // 앞뒤 쌍따옴표 제거 후 파싱
                    String cleanResponse = errorResponse.replaceAll("^\"|\"$", "");
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode jsonNode = mapper.readTree(cleanResponse);
                    errorCode = String.valueOf(jsonNode.get("error_code").asInt());  // int를 String으로 변환
                    errorMessage = jsonNode.get("error_message").asText();

                    log.info("Parsed errorMessage={}", errorMessage);
                    log.info("Parsed errorCode={}", errorCode);
                } catch (JsonProcessingException ex) {
                    log.error("Error parsing JSON response", ex);
                    errorMessage = e.getMessage();
                }
            } else {
                errorMessage = e.getMessage();
            }
            log.info("errorMessage={}", errorMessage);
            log.info("errorCode={}", errorCode);
            log.info("errorCode={}", errorCode);

            PaymentHistory failureHistory = PaymentHistory.toFailureHistory(
                    payment,
                    errorMessage,
                    errorCode
            );
            paymentHistoryRepository.save(failureHistory);

        } catch (Exception ex) {
            log.error("결제 실패 처리 중 오류 발생", ex);
        }
    }



//    @Transactional(propagation = Propagation.REQUIRES_NEW)
//    public void handlePaymentFailure(Long reservationId, Exception e) {
//
//        log.error("결제 실패 - reservationId: {}, error: {}", reservationId, e.getMessage());
//
//        try {
//            log.info("Payment 조회 시도 - reservationId: {}", reservationId);
//            Payment payment = paymentRepository.findByReservationId(reservationId)
//                    .orElseThrow(() -> new EntityNotFoundException("결제 정보를 찾을 수 없습니다."));
//
//            payment.updateStatus(PaymentStatus.PAYMENT_FAILED);
//            Payment savedPayment = paymentRepository.save(payment);
//            log.info("저장된 Payment  - paymentId: {}", savedPayment.getId());
//            log.info("저장된 Payment  - reservationId: {}", savedPayment.getReservation().getId());
//
//
//            PaymentHistory failureHistory = PaymentHistory.toFailureHistory(payment, e.getMessage());
//            paymentHistoryRepository.save(failureHistory);
//
//        } catch (Exception ex) {
//            log.error("결제 실패 처리 중 오류 발생", ex);
//        }
//    }
}