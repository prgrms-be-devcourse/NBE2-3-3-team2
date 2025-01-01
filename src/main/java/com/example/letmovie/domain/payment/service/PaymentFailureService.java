package com.example.letmovie.domain.payment.service;

import com.example.letmovie.domain.payment.dto.response.PaymentResponse;
import com.example.letmovie.domain.payment.entity.Payment;
import com.example.letmovie.domain.payment.entity.PaymentHistory;
import com.example.letmovie.domain.payment.entity.PaymentStatus;
import com.example.letmovie.domain.payment.repository.PaymentHistoryRepository;
import com.example.letmovie.domain.payment.repository.PaymentRepository;
import com.example.letmovie.global.exception.ErrorCodes;
import com.example.letmovie.global.exception.PaymentException;
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
            Payment payment = paymentRepository.findByReservationId(reservationId)
                    .orElseThrow(() -> new PaymentException(ErrorCodes.PAYMENT_NOT_FOUND));


            payment.updateStatus(PaymentStatus.PAYMENT_FAILED);
            paymentRepository.save(payment);

            // 에러 응답 파싱
            String errorCode = "";
            String errorMessage = "";

            if (e instanceof HttpClientErrorException) {
                String errorResponse = ((HttpClientErrorException) e).getResponseBodyAsString();
                try {
                    String cleanResponse = errorResponse.replaceAll("^\"|\"$", "");
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode jsonNode = mapper.readTree(cleanResponse);
                    errorCode = String.valueOf(jsonNode.get("error_code").asInt());
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

            PaymentHistory failureHistory = PaymentHistory.toFailureHistory(
                    payment,
                    errorMessage,
                    errorCode
            );
            paymentHistoryRepository.save(failureHistory);

        }
    }
