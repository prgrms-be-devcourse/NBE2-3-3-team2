package com.example.letmovie.domain.auth.security;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    /**
     * FilterChain 적용 제외
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/js/")
                || path.startsWith("/css/")
                || path.startsWith("/images/")
                || path.equals("/")
                || path.equals("/signup")
                || path.equals("/login")
                || path.startsWith("/movie/")
                || path.equals("/token/refresh");
    }

    /**
     * JWT 토큰 인증 정보 확인
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = resolveToken(request);

        try {
            if (token != null && jwtTokenProvider.validateToken(token)) {
                String email = jwtTokenProvider.getEmailFromToken(token); // 토큰에서 사용자 이메일 추출
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(email); // 사용자 정보 로드

                // 인증된 사용자 정보를 SecurityContext에 설정
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                // SecurityContext 로그 출력
                logger.info("SecurityContext 인증 정보: " + SecurityContextHolder.getContext().getAuthentication());

                // 요청 속성에 사용자 이메일 설정
                request.setAttribute("userEmail", email);
                logger.info("userEmail " + email);
            }
            if (token == null || !jwtTokenProvider.validateToken(token)) {
                logger.warn("JWT 토큰이 유효하지 않습니다.");
                return;
            }
        } catch (ExpiredJwtException e) {
            logger.warn("Access Token이 만료되었습니다.", e);

            // AJAX 요청인지 확인
            if (isAjaxRequest(request)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Access Token Expired\"}");
            } else {
                response.sendRedirect("/token/refresh");
            }
            return; // 필터 체인 중단
        } catch (Exception e) {
            logger.error("JWT 인증 실패: " + e.getMessage(), e);

            // AJAX 요청인지 확인
            if (isAjaxRequest(request)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Unauthorized: Invalid JWT Token\"}");
            } else {
                // 일반 요청에 대한 처리
                response.sendRedirect("/login");
            }
            return;
        }

        filterChain.doFilter(request, response); // 다음 필터로 요청 전달
    }

    /**
     * 쿠키에서 JWT 추출하는 메서드
     */
    private String resolveToken(HttpServletRequest request) {
        // 쿠키 배열에서 accessToken 찾기
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    logger.debug("Access Token 추출: " + cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        // 쿠키에 accessToken이 없으면 null 반환
        logger.warn("Access Token이 쿠키에서 발견되지 않았습니다.");
        return null;
    }

    /**
     * AJAX 요청인지 확인하는 메서드
     */
    private boolean isAjaxRequest(HttpServletRequest request) {
        String requestedWith = request.getHeader("X-Requested-With");
        return "XMLHttpRequest".equals(requestedWith); // AJAX 요청은 X-Requested-With 헤더를 포함
    }
}
