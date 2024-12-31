package com.example.letmovie.domain.payment.repository;

import com.example.letmovie.domain.payment.entity.Payment;
import com.example.letmovie.domain.payment.entity.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment,Long> {

    Optional<Payment> findByReservationId(long reservationId);


    List<Payment> findByMemberId(Long id);
}
