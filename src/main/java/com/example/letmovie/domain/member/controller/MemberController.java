package com.example.letmovie.domain.member.controller;

import com.example.letmovie.domain.member.dto.request.SignupRequestDTO;
import com.example.letmovie.domain.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/signup")
    @Operation(summary = "회원 가입 뷰")
    public String signupPage(Model model) {
        model.addAttribute("memberForm", new SignupRequestDTO());
        return "signup";
    }

    @PostMapping("/signup")
    @Operation(summary = "회원 가입 처리 API")
    public ResponseEntity<String> signup(@RequestBody SignupRequestDTO request, Model model) {
        try {
            memberService.signup(request);
            // 성공
            return ResponseEntity.status(HttpStatus.CREATED).body("회원 가입이 성공적으로 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            //  중복 닉네임 또는 이메일
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // 기타 예외 발생 시
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원 가입 처리 중 오류가 발생했습니다.");
        }
    }
}
