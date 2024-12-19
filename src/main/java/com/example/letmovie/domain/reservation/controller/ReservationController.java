package com.example.letmovie.domain.reservation.controller;

import com.example.letmovie.domain.movie.entity.Showtime;
import com.example.letmovie.domain.reservation.entity.Screen;
import com.example.letmovie.domain.reservation.entity.Seat;
import com.example.letmovie.domain.reservation.service.ScreenService;
import com.example.letmovie.domain.reservation.service.SeatService;
import com.example.letmovie.domain.reservation.service.ShowtimeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ReservationController {

    private final ShowtimeService showtimeService;
    private final SeatService seatService;
    private final ScreenService screenService;

    /**
     * 1. 영화 선택
     * 2. 극장을 선택해야함 조건 1번 OK
     * 2-1 관리자가 극장, 상영관,영화 상영시간을 배정 해놨어야 함. -> sql 더미데이터로 OK
     * 2-2  극장클릭 -> db에서 극장id와 같은 상영관 나열
     * 2-3 상영관 클릭-> db에서 상영관과 극장id가 같은 쇼타임 조회-> 그럼 상영 시간 알 수 있음.
     * <p>
     * 3. 그럼 극장은 영화id타고 상영관 id타고 극장이름으로 조회?
     * <p>
     * 4.시간은 영화상영시간을 리스트로 나열한 뒤
     * 영화상영시간 표시,
     * 영화 id타고 영화이름 표시
     * 상영관id타고 상영관 표시 후
     * <p>
     * 시간대랑 관 선택후 결제하기 클릭하면 될듯?
     * <p>
     * 더미데이터 넣고 확인
     */


    //예약 홈화면
    @GetMapping("/reservation")
    public String reservation() {
        return "reservation/reservationHome";
    }

    //AJAX통신
    //날짜에 맞는 영화상영시간 테이블 찾기.
    //쇼타임 테이블을 통해 극장찾기. 극장 id값과 같은 상영관. 상영관 id와 같은 영화 id찾아서 리스트로 내리기.
    @ResponseBody
    @PostMapping("/selectDate")
    public Map<String, Object> selectDate(@RequestBody Map<String, String> payload) {
        String selectedDate = payload.get("date");

        // 서버에서 받은 날짜 확인 (로그 출력) test 지워야함.
        log.info("====selectDate====");
        log.info("선택한 날짜 = {}", selectedDate);
        log.info("==================");

        //selectedDate = 2024-12-18
        List<String> movieNames = showtimeService.findMovieNameByDate(selectedDate);
        System.out.println(movieNames.size());

        // 필요한 데이터 가공 후 클라이언트에 반환
        return Map.of("movieNames", movieNames);
    }

    @ResponseBody
    @PostMapping("/selectMovie")
    public Map<String, Object> selectTheater(@RequestBody Map<String, String> payload) {
        String movieName  = payload.get("movieName");
        String date = payload.get("date");

        log.info("====selectMovie========");
        log.info("moviename = {}", movieName);
        log.info("date = {}", date);
        log.info("======================");

        // 영화 ID로 극장 리스트 조회
        List<Map<String, String>> theaters = showtimeService.findTheatersByMovieNameAndDate(movieName, date);

        // 극장 리스트 반환
        return Map.of("theaters", theaters);
    }

    @ResponseBody
    @PostMapping("/selectShowtimes")
    public Map<String, Object> selectShowtimes(@RequestBody Map<String, String> payload) {
        String movieName = payload.get("movieName");
        String date = payload.get("date");
        String theaterName = payload.get("theaterName");

        log.info("====selectShowtimes=====");
        log.info("movieName: " + movieName);
        log.info("date: " + date);
        log.info("theaterName: " + theaterName);
        log.info("=======================");

        List<Map<String, String>> showtimes = showtimeService.findShowtimeByDateAndMovieNameAndTheater(movieName, date, theaterName);

        return Map.of("showtimes", showtimes);
    }


    @ResponseBody
    @PostMapping("/saveSelection/seat")
    public String saveSeat(@RequestBody Map<String, String> payload, Model model) {
//        String date = payload.get("date");
//        String movieName = payload.get("movie");
//        String theaterName = payload.get("theater");
//        String screenName = payload.get("screen");


        //이 부분에 예매중으로 등록해도 좋을 듯? 좌석 선택부터 갈 수 있도록


        return "hi";
    }

    @GetMapping("/seatSelection")
    public String seatSelection(@RequestParam("showtimeId") Long showtimeId, Model model) {
        //1. 상영관에서 좌석을 가져오고
        //2. showTime 아이디만 가져오면 상영관 통해서 좌석 다 가져오기 가능..
        //3. 가져온 좌석이랑 프론트랑 id 매칭 시키면 될듯?

        //db를 통해 showtime을 가져와서 show타임을 통해 상영관,
        //상영관을 통해 좌석을 가져와서 for문으로  자석 정보들을 뿌려줌.. 예매 불가능은 회색 가능은 밝은색
        //좌석 열 좌석 번호 좌석 가격 종류 등등 전부 다
        Showtime showtime = showtimeService.findById(Long.valueOf(showtimeId))
                .orElseThrow(() -> new RuntimeException("쇼타임 아이디 없따잉: " + showtimeId));
//        Screen screen = screenService.findById(showtime.getScreen().getId()).orElseThrow(() -> new RuntimeException("스크린 정보를 찾을 수 없습니다:" + showtime.getScreen().getId()));

        Screen screen = showtime.getScreen();

        List<Seat> seats = showtime.getScreen().getSeats();
        List<Seat> sortedSeats = seats.stream()
                .sorted(Comparator.comparingInt(Seat::getSeatLow)
                        .thenComparingInt(Seat::getSeatCol))
                .collect(Collectors.toList());

        //최대 행 계산
        int maxRow = sortedSeats.stream()
                .mapToInt(Seat::getSeatLow)
                .max()
                .orElse(0);

        List<Integer> rows = IntStream.rangeClosed(1, maxRow)
                .boxed()
                .collect(Collectors.toList());

        log.info("-----------> screen = {}", screen.getScreenName());
        log.info("Showtime: {}", showtime);
        log.info("Screen: {}", screen);
        log.info("Seats: {}", seats);
        log.info("-----------> screen = {}", screen);

        model.addAttribute("seats", sortedSeats);
        model.addAttribute("screen", screen);
        model.addAttribute("showtime", showtime);
        model.addAttribute("rows", rows);

        return "reservation/seatSelection";
    }

}
