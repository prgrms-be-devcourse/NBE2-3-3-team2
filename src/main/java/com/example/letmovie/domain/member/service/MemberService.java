package com.example.letmovie.domain.member.service;

import com.example.letmovie.domain.member.dto.SignupRequestDTO;
import com.example.letmovie.domain.member.entity.Authority;
import com.example.letmovie.domain.member.entity.Grade;
import com.example.letmovie.domain.member.entity.Member;
import com.example.letmovie.domain.member.entity.MemberStatus;
import com.example.letmovie.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;


    // 회원가입
    public void signup(SignupRequestDTO request) {
        // 닉네임 중복 검사
        if (memberRepository.existsByNickname(request.getNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        // 이메일 중복 검사
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 회원 저장
        Member member = Member.builder()
                .nickname(request.getNickname())
                .email(request.getEmail())
                .password(encodedPassword)
                .birthDate(request.getBirthDate())
                .authority(Authority.ROLE_USER)
                .grade(Grade.GENERAL)
                .memberStatus(MemberStatus.AVAILABLE)
                .build();

        memberRepository.save(member);
    }
}
