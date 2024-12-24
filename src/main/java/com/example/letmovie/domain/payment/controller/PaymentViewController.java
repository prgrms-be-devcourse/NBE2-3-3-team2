package com.example.letmovie.domain.payment.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/payment")
public class PaymentViewController {

    @GetMapping("/success")
    public String showPaymentSuccess() {
        return "payment-success";
    }

    @GetMapping("/ready")
    public String myPayment() {
        return "payment-ready";
    }

    @GetMapping("/paymentlist")
    public String myPaymentPage() {
        return "paymentlist";
    }
}
