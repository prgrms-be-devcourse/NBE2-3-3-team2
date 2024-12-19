package com.example.letmovie.domain.payment.repository;

import com.example.letmovie.domain.payment.entity.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory,Long> {

}
