package com.example.letmovie.domain.reservation.controller;

import com.example.letmovie.domain.reservation.service.ShowtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ReservationController {

    private final ShowtimeService showtimeService;

    /**
     * 1. 영화 선택
     * 2. 극장을 선택해야함 조건 1번 OK
     * 	2-1 관리자가 극장, 상영관,영화 상영시간을 배정 해놨어야 함. -> sql 더미데이터로 OK
     * 	2-2  극장클릭 -> db에서 극장id와 같은 상영관 나열
     * 	2-3 상영관 클릭-> db에서 상영관과 극장id가 같은 쇼타임 조회-> 그럼 상영 시간 알 수 있음.
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

    // 더미 데이터: 영화 ID에 따른 상영관 목록
    private static final Map<String, List<String>> SCREEN_DATA = Map.of(
            "1", List.of("상영관 A1", "상영관 A2", "상영관 A3"),
            "2", List.of("상영관 B1", "상영관 B2"),
            "movie3", List.of("상영관 C1", "상영관 C2", "상영관 C3")
    );



    //예약 홈화면
    @GetMapping("/reservation")
    public String reservation() {
        return "reservation/reservationHome";
    }

    //AJAX통신
    //날짜에 맞는 영화상영시간 테이블 찾기.
    //쇼타임 테이블을 통해 극장찾기. 극장 id값과 같은 상영관. 상영관 id와 같은 영화 id찾아서 리스트로 내리기.
    @ResponseBody
    @RequestMapping("/selectDate")
    public Map<String, Object> selectDate(@RequestBody Map<String, String> payload) {
        String selectedDate = payload.get("date");

        // 서버에서 받은 날짜 확인 (로그 출력) test 지워야함.
        System.out.println("선택한 날짜: " + selectedDate);

        //selectedDate = 2024-12-18
        List<String> movieNames = showtimeService.findMovieNameByDate(selectedDate);
        System.out.println(movieNames.size());

        // 필요한 데이터 가공 후 클라이언트에 반환
        return Map.of("movieNames", movieNames);
    }

    @ResponseBody
    @RequestMapping("/selectMovie")
    public Map<String, Object> selectTheater(@RequestBody Map<String, String> payload) {
        String movieName  = payload.get("movieName");
        String date = payload.get("date");

        System.out.println(movieName);
        System.out.println(date);

        // 영화 ID로 극장 리스트 조회
        List<Map<String, String>> theaters = showtimeService.findTheatersByMovieNameAndDate(movieName, date);

        // 극장 리스트 반환
        return Map.of("theaters", theaters );
    }






//    //아작스통신
//    // 영화에 맞는 극장 응답.
//    @ResponseBody
//    @RequestMapping("/getScreens")
//    public Map<String, List<String>> getTheaters(@RequestParam("movie") String movieId) {
//        // 영화 ID에 맞는 상영관 목록 반환
//        List<String> screens = SCREEN_DATA.getOrDefault(movieId, Collections.emptyList());
//
//        for (String screen : screens) {
//            System.out.println(screen);
//        }
//
//        return Map.of("screens", screens);
//        }

}
