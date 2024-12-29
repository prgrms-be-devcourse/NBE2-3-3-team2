package com.example.letmovie.domain.auth.controller;

import com.example.letmovie.domain.auth.repository.TokenBlacklistRepository;
import com.example.letmovie.domain.auth.security.JwtTokenProvider;
import com.example.letmovie.domain.auth.service.AuthService;
import com.example.letmovie.domain.member.dto.request.LoginRequestDTO;
import com.example.letmovie.domain.member.dto.response.LoginResponseDTO;
import com.example.letmovie.domain.member.entity.Member;
import com.example.letmovie.domain.member.repository.MemberRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final MemberRepository memberRepository;
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthService authService;

    // TODO 배포 시, setSecure() true 처리 후 배포

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginForm", new LoginRequestDTO());
        return "login";
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request, HttpServletResponse response, Model model) {

        try {
            LoginResponseDTO loginResponse = authService.login(request);

            // 쿠키 설정
            Cookie accessTokenCookie = new Cookie("accessToken", loginResponse.getAccessToken());
            accessTokenCookie.setHttpOnly(true);
            accessTokenCookie.setSecure(false); // 보안상 쿠키가 HTTPS에서만 전송되도록 보장해야 하나 테스트 환경이므로 해당 속성 false 처리
            accessTokenCookie.setPath("/");
            accessTokenCookie.setMaxAge(60 * 15); // 15분

            Cookie refreshTokenCookie = new Cookie("refreshToken", loginResponse.getRefreshToken());
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(false); // 보안상 쿠키가 HTTPS에서만 전송되도록 보장해야 하나 테스트 환경이므로 해당 속성 false 처리
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7일

            response.addCookie(accessTokenCookie);
            response.addCookie(refreshTokenCookie);
            return ResponseEntity.status(HttpStatus.CREATED).body("로그인 성공");
        } catch (IllegalArgumentException e) {
            // 잘못된 정보로 로그인 시
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // 기타 예외 발생
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("로그인 처리 중 오류가 발생했습니다.");
        }
    }

    /*
     * 토큰 만료 시 처리
     * */
    @PostMapping("/token/refresh")
    public ResponseEntity<?> refresh(@CookieValue("refreshToken") String refreshToken, HttpServletResponse response) {
        try {
            // Refresh Token이 블랙리스트에 있는지 확인
            if (tokenBlacklistRepository.findByRefreshToken(refreshToken).isPresent()) {
                return ResponseEntity.status(403).body("Refresh Token이 블랙리스트에 포함되어 있습니다.");
            }

            // Refresh Token 유효성 검증
            if (!jwtTokenProvider.validateToken(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 Refresh Token입니다.");
            }

            // Refresh Token에서 사용자 이메일 추출
            String email = jwtTokenProvider.getEmailFromToken(refreshToken);

            // 사용자 정보 확인
            Member member = memberRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

            // 새로운 Access Token 생성
            String newAccessToken = jwtTokenProvider.createAccessToken(member.getEmail());

            // 새 Access Token을 HTTP-Only 쿠키로 설정
            Cookie accessTokenCookie = new Cookie("accessToken", newAccessToken);
            accessTokenCookie.setHttpOnly(true);
            accessTokenCookie.setSecure(false);
            accessTokenCookie.setPath("/");
            accessTokenCookie.setMaxAge(60 * 15); // 15분

            response.addCookie(accessTokenCookie);

            return ResponseEntity.ok("Access Token 재발급 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh Token 검증 실패");
        }
    }

    /*
     * 로그아웃
     * */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue("refreshToken") String refreshToken, HttpServletResponse response) {
        try {
            // Refresh Token 만료 시간
            LocalDateTime expiryDate = LocalDateTime.now().plusDays(7);

            // 블랙리스트에 추가 (Refresh Token 관리)
            authService.logout(refreshToken, expiryDate);

            // SecurityContext 정리
            SecurityContextHolder.clearContext();

            // Access Token 삭제
            Cookie accessTokenCookie = new Cookie("accessToken", null);
            accessTokenCookie.setHttpOnly(true);
            accessTokenCookie.setSecure(false); // 보안상 쿠키가 HTTPS에서만 전송되도록 보장해야 하나 테스트 환경이므로 해당 속성 false 처리
            accessTokenCookie.setPath("/");
            accessTokenCookie.setMaxAge(0); // 즉시 만료

            // Refresh Token 삭제
            Cookie refreshTokenCookie = new Cookie("refreshToken", null);
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(false); // 보안상 쿠키가 HTTPS에서만 전송되도록 보장해야 하나 테스트 환경이므로 해당 속성 false 처리
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge(0); // 즉시 만료

            // 쿠키 설정
            response.addCookie(accessTokenCookie);
            response.addCookie(refreshTokenCookie);

            // 리다이렉트 응답
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", "/login")
                    .build();
        }

        catch (Exception e) {
            // 기타 예외 발생
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("로그아웃 처리 중 오류가 발생했습니다.");
        }
    }
}
