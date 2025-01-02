package com.example.letmovie.domain.auth.service;

import com.example.letmovie.domain.auth.security.JwtTokenProvider;
import com.example.letmovie.domain.member.dto.request.LoginRequestDTO;
import com.example.letmovie.domain.member.dto.response.LoginResponseDTO;
import com.example.letmovie.domain.member.entity.Member;
import com.example.letmovie.domain.member.repository.MemberRepository;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlacklistService tokenBlacklistService;
    private final CustomUserDetailsService customUserDetailsService;

    public LoginResponseDTO login(LoginRequestDTO request) {
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(member.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getEmail());

        return new LoginResponseDTO(accessToken, refreshToken);
    }

    /**
     *  Refresh Token을 블랙리스트에 추가
     */
    public void logout(String refreshToken, LocalDateTime expiryDate) {
        tokenBlacklistService.addToBlacklist(refreshToken, expiryDate);
    }

    /**
     *  새 인증정보를 업데이트 함
     */
    public Cookie updateNewAuthentication() {
        UserDetails updatedUserDetails = customUserDetailsService.loadUserByUsername(
                SecurityContextHolder.getContext().getAuthentication().getName()
        );
        System.out.println("updatedUserDetails : " + updatedUserDetails.toString());
        UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
                updatedUserDetails,
                null,
                updatedUserDetails.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(newAuth);

        String newAccessToken = jwtTokenProvider.createAccessToken(updatedUserDetails.getUsername());

        Cookie accessTokenCookie = new Cookie("accessToken", newAccessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(false); // 보안상 쿠키가 HTTPS에서만 전송되도록 보장해야 하나 테스트 환경이므로 해당 속성 false 처리
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(60 * 15); // 15분

        return accessTokenCookie;
    }
}
