@file:JvmName("SecurityUtil")

package com.example.letmovie.domain.auth.util

import com.example.letmovie.domain.member.entity.Member
import com.example.letmovie.domain.member.repository.MemberRepository
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import java.util.Optional

private lateinit var memberRepository: MemberRepository

fun setMemberRepository(repository: MemberRepository) {
    memberRepository = repository
}

/**
 * 현재 인증된 멤버 정보를 가져옴
 */
fun getCurrentMember(): Optional<Member> {
    val email = getCurrentMemberEmail() ?: return Optional.empty()
    return Optional.ofNullable(memberRepository.findByEmail(email).orElse(null))
}

/**
 * 현재 인증된 사용자의 이메일을 가져옴
 */
fun getCurrentMemberEmail(): String? {
    val authentication = SecurityContextHolder.getContext().authentication
    if (authentication == null || !authentication.isAuthenticated || authentication.principal == "anonymousUser") {
        return null
    }

    return when (val principal = authentication.principal) {
        is UserDetails -> principal.username
        is String -> principal
        else -> null
    }
}

/**
 * 현재 인증된 사용자의 역할을 확인 (ROLE_USER, ROLE_ADMIN 등)
 */
fun hasRole(role: String): Boolean {
    val authentication = SecurityContextHolder.getContext().authentication

    return authentication?.authorities?.any { it.authority == role } ?: false
}

/**
 * 현재 인증 정보를 가져옴
 */
fun getAuthentication(): Authentication? {
    return SecurityContextHolder.getContext().authentication
}

/**
 * 인증 정보 설정 (테스트 용)
 */
fun setAuthentication(authentication: Authentication) {
    SecurityContextHolder.getContext().authentication = authentication
}