package com.example.letmovie.domain.payment.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/payment")
public class PaymentViewController {

    @GetMapping("/test")
    public String paymentTest() {
        return "payment-test";
    }

    @GetMapping("/success")
    public String showPaymentSuccess() {
        return "payment-success";
    }
}
