package com.example.letmovie.domain.reservation.controller;

import com.example.letmovie.domain.movie.entity.Showtime;
import com.example.letmovie.domain.reservation.dto.request.DateRequestDTO;
import com.example.letmovie.domain.reservation.dto.request.ShowTimeRequestDTO;
import com.example.letmovie.domain.reservation.dto.request.TheaterRequestDTO;
import com.example.letmovie.domain.reservation.dto.response.MovieNamesResponseDTO;
import com.example.letmovie.domain.reservation.dto.response.ShowTimeResponseDTO;
import com.example.letmovie.domain.reservation.dto.response.TheaterResponseDTO;
import com.example.letmovie.domain.reservation.entity.Screen;
import com.example.letmovie.domain.reservation.entity.Seat;
import com.example.letmovie.domain.reservation.service.ShowtimeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ReservationController {

    private final ShowtimeService showtimeService;

    @GetMapping("/reservation")
    public String reservation() {
        return "reservation/reservationHome";
    }

    /**
     * 날짜 선택 시 영화 리스트 찾기. (쿼리 1번)
     */
    @ResponseBody
    @PostMapping("/selectDate")
    public MovieNamesResponseDTO selectDate(@RequestBody DateRequestDTO selectDateDTO) {
        String selectedDate = selectDateDTO.getDate(); //selectedDate = ex)2024-12-18
        return showtimeService.findMovieNameByDate(selectedDate);
    }

    /**
     * 날짜,영화 선택 시 극장 리스트 찾기. (쿼리 2번)
     */
    @ResponseBody
    @PostMapping("/selectMovie")
    public List<TheaterResponseDTO> selectTheater(@RequestBody TheaterRequestDTO theaterRequestDTO) {
        return showtimeService.findTheatersByMovieNameAndDate(
                theaterRequestDTO.getMovieName(),
                theaterRequestDTO.getDate()
        );
    }

    /**
     * 날짜,영화, 극장 선택 시 ShowTime 리스트 찾기(상영관, 상영시간, 총좌석, 예약가능 좌석). (쿼리 x)
     */
    @ResponseBody
    @PostMapping("/selectShowtimes")
    public List<ShowTimeResponseDTO> selectShowTimes(@RequestBody ShowTimeRequestDTO showTimeRequestDTO) {
        return showtimeService.findShowtimeByDateAndMovieNameAndTheater(
                showTimeRequestDTO.getMovieName(),
                showTimeRequestDTO.getDate(),
                showTimeRequestDTO.getTheaterName());
    }

    @ResponseBody
    @PostMapping("/saveSelection/seat")
    public String saveSeat(@RequestBody Map<String, String> payload, Model model) {
//        String date = payload.get("date");
//        String movieName = payload.get("movie");
//        String theaterName = payload.get("theater");
//        String screenName = payload.get("screen");
        //이 부분에 예매중으로 등록 해야 함. 좌석 선택부터 갈 수 있도록
        return "ex)예매중";
    }

    @GetMapping("/seatSelection")
    public String seatSelection(@RequestParam("showtimeId") Long showtimeId, Model model) {
        Showtime showtime = showtimeService.findById(Long.valueOf(showtimeId))
                .orElseThrow(() -> new RuntimeException("쇼타임 아이디 없음: " + showtimeId)); //에러 만들어 줘야 함.

//        Screen screen = screenService.findById(showtime.getScreen().getId()).orElseThrow(() -> new RuntimeException("스크린 정보를 찾을 수 없습니다:" + showtime.getScreen().getId()));

        Screen screen = showtime.getScreen();

        //
        List<Seat> seats = showtime.getScreen().getSeats();
        List<Seat> sortedSeats = seats.stream()
                .sorted(Comparator.comparingInt(Seat::getSeatLow)
                        .thenComparingInt(Seat::getSeatCol))
                .collect(Collectors.toList());

        //최대 행 계산
        int maxRow = sortedSeats.stream()
                .mapToInt(Seat::getSeatLow) // seatLow 값 돌면서 추출
                .max() // 가장 큰 값 찾기
                .orElse(0);

        //rows는 [1, 2, 3, ..., maxRow] 형태의 리스트
        List<Integer> rows = IntStream.rangeClosed(1, maxRow) //숫자 1부터 maxRow까지의 연속된 정수 스트림을 생성
                .boxed() // int 타입의 스트림을 Integer 객체 스트림으로 변환
                .collect(Collectors.toList()); //스트림의 모든 요소를 리스트

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

    //필드 예약번호가 예매아이디, 사용자 아이디
}
