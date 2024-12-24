package com.example.letmovie.domain.payment.repository;

import com.example.letmovie.domain.payment.entity.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory,Long> {

//    List<PaymentHistory> findByMemberId(Long id);
}
