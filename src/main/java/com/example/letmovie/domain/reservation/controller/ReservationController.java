package com.example.letmovie.domain.reservation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
public class ReservationController {
    /**
     * 1. 영화 선택
     * 2. 극장을 선택해야함 조건 o
     * 	2-1 관리자가 극장, 상영관,영화 상영시간을 배정 해놨어야 함.
     *
     * 3. 그럼 극장은 영화id타고 상영관 id타고 극장이름으로 조회?
     *
     * 4.시간은 영화상영시간을 리스트로 나열한 뒤
     * 	영화상영시간 표시,
     * 영화 id타고 영화이름 표시
     * 상영관id타고 상영관 표시 후
     *
     * 시간대랑 관 선택후 결제하기 클릭하면 될듯?
     *
     * 더미데이터 넣고 확인
     */

    //예약 홈화면
    @GetMapping("/reservation")
    public String reservation() {
        return "reservation/reservationHome";
    }


    // 예시 데이터: 영화 ID에 따른 상영관 목록
    private static final Map<String, List<String>> THEATER_DATA = Map.of(
            "1", List.of("상영관 A1", "상영관 A2", "상영관 A3"),
            "2", List.of("상영관 B1", "상영관 B2"),
            "movie3", List.of("상영관 C1", "상영관 C2", "상영관 C3")
    );

    //아작스통신
    // 영화에 맞는 극장 응답.
    @ResponseBody
    @RequestMapping("/getTheaters")
    public Map<String, List<String>> getTheaters(@RequestParam("movie") String movieId) {
        // 영화 ID에 맞는 상영관 목록 반환
        List<String> theaters = THEATER_DATA.getOrDefault(movieId, Collections.emptyList());

        for (String theater : theaters) {
            System.out.println(theater);
        }

        return Map.of("theaters", theaters);
        }

}
