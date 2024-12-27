package com.example.letmovie.domain.payment.service;

import com.example.letmovie.domain.member.entity.Member;
import com.example.letmovie.domain.payment.dto.response.PaymentHistoryResponse;
import com.example.letmovie.domain.payment.entity.PaymentHistory;
import com.example.letmovie.domain.payment.repository.PaymentHistoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentHistoryService {

    private final PaymentHistoryRepository paymentHistoryRepository;

//    @Transactional(readOnly = true)
//    public PaymentHistoryResponse.Info getPaymentHistoryDetail(Long paymentId) {
//        PaymentHistory paymentHistory = paymentHistoryRepository.findById(paymentId)
//                .orElseThrow(()-> new EntityNotFoundException("결제 내역 없음."));
//
//        return PaymentHistoryResponse.Info.from(paymentHistory);
//    }
//
//
    public void deletePaymentHistory(Long paymentId) {
        PaymentHistory paymentHistory = paymentHistoryRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("결제 내역 없음"));
    }
}
