package com.example.letmovie.domain.admin.service

import com.example.letmovie.domain.admin.repository.AdminMemberRepository
import com.example.letmovie.domain.admin.repository.AdminPaymentHistoryRepository
import com.example.letmovie.domain.member.entity.Member
import com.example.letmovie.domain.payment.entity.PaymentHistory
import org.springframework.stereotype.Service

@Service
class AdminPaymentServiceImpl(
    private val adminMemberRepository: AdminMemberRepository,
    private val adminPaymentHistoryRepository: AdminPaymentHistoryRepository
){

    // 닉네임으로 회원 조회
    fun findMemberByName(nickname: String): List<Member> {
        return adminMemberRepository.findByNicknameContainingIgnoreCase(nickname)
    }

    // 특정 회원의 결제 내역 조회
    fun findPaymentHistoryByMemberId(memberId: Long): List<PaymentHistory> {
        val partnerUserId = memberId.toString()
        return adminPaymentHistoryRepository.findByPartnerUserId(partnerUserId)
    }
}
