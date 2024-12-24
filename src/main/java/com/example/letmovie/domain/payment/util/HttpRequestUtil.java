package com.example.letmovie.domain.payment.util;

import com.example.letmovie.domain.payment.dto.request.PaymentRequest;
import com.example.letmovie.domain.payment.entity.PaymentHistory;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class HttpRequestUtil {

    private final RestTemplate restTemplate;

    public HttpHeaders createHeaders(String secretkey) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "SECRET_KEY " + secretkey);
        headers.add("Content-Type", "application/json");
        return headers;
    }

    public <T> T post(String url, Map<String, String> body, HttpHeaders headers, Class<T> responseType) {
            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<T> response = restTemplate.postForEntity(url, requestEntity, responseType);
            return response.getBody();
    }

    // 결제 준비 파라미터 생성
    public Map<String, String> createReadyParams(PaymentRequest.Info request) {

        Map<String, String> params = new HashMap<>();
        params.put("cid", "TC0ONETIME");
        params.put("partner_order_id", String.valueOf(request.reservation_id()));
        params.put("partner_user_id", String.valueOf(request.member_id())); // 주문 멤버id
        params.put("item_name", request.name()); // 영화 이름
        params.put("quantity", "1"); // 티켓 수량
        params.put("total_amount", String.valueOf(request.totalPrice())); //전체 가격
        params.put("tax_free_amount", "0");
        params.put("approval_url", "http://localhost:8080/payment/success");
        params.put("cancel_url", "http://localhost:8080/payment/cancel");
        params.put("fail_url", "http://localhost:8080/payment/fail");

        return params;
    }

    public Map<String, String> createApprovalParams(String pgToken, String tid, String partnerUserId,
                                                    String partnerOrderId, String cid) {
        Map<String, String> params = new HashMap<>();
        params.put("cid", cid);
        params.put("tid", tid);
        params.put("pg_token", pgToken); // 오류시 테스트
        params.put("partner_order_id", partnerOrderId);
        params.put("partner_user_id", partnerUserId);

        return params;
    }

    public Map<String, String> createCancelParams(PaymentHistory paymentHistory) {
        log.info("PaymentHistory 정보 - id: {}, tid: {}, amount: {}",
                paymentHistory.getId(),
                paymentHistory.getTid(),
                paymentHistory.getAmount());
        Map<String, String> params = new HashMap<>();
        params.put("cid", paymentHistory.getCid());
        params.put("tid", paymentHistory.getTid());
        params.put("cancel_amount", String.valueOf(paymentHistory.getAmount()));
        params.put("cancel_tax_free_amount", "0");        // 취소 비과세 금액
        params.put("cancel_vat_amount", "0");             // 취소 부가세 금액

        log.info("Created cancel parameters - cid: {}, tid: {}, amount: {}, tax_free: {}, vat: {}",
                paymentHistory.getCid(),
                paymentHistory.getTid(),
                paymentHistory.getAmount(),
                0,  // tax_free amount
                0); // vat amount


        return params;
    }

}