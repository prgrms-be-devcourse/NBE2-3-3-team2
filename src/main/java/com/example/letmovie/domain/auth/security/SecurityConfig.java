package com.example.letmovie.domain.auth.security;

import com.example.letmovie.domain.auth.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .cors().and()   // CORS 설정 활성화
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // OPTIONS 요청 허용ㅍ
                        .requestMatchers("/js/**", "/css/**", "/images/**")
                        .permitAll()
                        .requestMatchers( "/favicon.ico", "/","/send-email", "/swagger-ui/**", "/v3/api-docs/**", "/signup",  "/login", "/admin-login", "/logout","/status", "/token/refresh", "/movie/**", "/movies", "/api/search/**")
                        .permitAll()
                        .requestMatchers("/mypage/**").hasAuthority("ROLE_USER")
                        .requestMatchers("/private/**").hasAuthority("ROLE_USER")
                        .requestMatchers("/reservation/**").hasAuthority("ROLE_USER")
                        .requestMatchers("/payment/**").hasAuthority("ROLE_USER")
                        .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // JWT 사용 시 세션 비활성화 (스프링 시큐리티 내 세션 사용 방지)
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            System.out.println("exception : " + exception);
                            System.out.println("Access denied for: " + SecurityContextHolder.getContext().getAuthentication());
                            response.sendRedirect("/login"); // 인증되지 않은 사용자를 로그인 페이지로 리디렉션
                        })
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)  // JWT 필터 추가
                .logout()
                .logoutUrl("/custom-logout")
                .permitAll();
        return http.build();
    }
    // 일반 테스트 시에는 위 코드 주석처리 후 아래 코드 주석 풀어 사용
    /*public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .cors().and()   // CORS 설정 활성화
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/**")
                        .permitAll()
                );

        return http.build();
    }*/

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, CustomUserDetailsService customUserDetailsService) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }
}