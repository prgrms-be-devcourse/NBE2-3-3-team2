package com.example.letmovie.domain.reservation.controller;

import com.example.letmovie.domain.movie.entity.Showtime;
import com.example.letmovie.domain.reservation.dto.request.DateRequestDTO;
import com.example.letmovie.domain.reservation.dto.request.ReserveSeatsRequestDTO;
import com.example.letmovie.domain.reservation.dto.request.ShowTimeRequestDTO;
import com.example.letmovie.domain.reservation.dto.request.TheaterRequestDTO;
import com.example.letmovie.domain.reservation.dto.response.MovieNamesResponseDTO;
import com.example.letmovie.domain.reservation.dto.response.ReservationResponseDTO;
import com.example.letmovie.domain.reservation.dto.response.ShowTimeResponseDTO;
import com.example.letmovie.domain.reservation.dto.response.TheaterResponseDTO;
import com.example.letmovie.domain.reservation.entity.Screen;
import com.example.letmovie.domain.reservation.entity.Seat;
import com.example.letmovie.domain.reservation.service.ReservationService;
import com.example.letmovie.domain.reservation.service.ShowtimeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    private final ReservationService reservationService;

    @GetMapping("/reservation")
    public String reservation() {
        return "reservation/reservationHome";
    }

    /**
     * 날짜 선택 시 영화 리스트 찾기. (쿼리 1번)
     */
    @ResponseBody
    @PostMapping("/api/dates")
    public MovieNamesResponseDTO selectDate(@RequestBody DateRequestDTO selectDateDTO) {
        String selectedDate = selectDateDTO.getDate(); //selectedDate = ex)2024-12-18
        return showtimeService.findMovieNameByDate(selectedDate);
    }

    /**
     * 날짜,영화 선택 시 극장 리스트 찾기. (쿼리 2번)
     */
    @ResponseBody
    @PostMapping("/api/movies")
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
    @PostMapping("/api/showtimes")
    public List<ShowTimeResponseDTO> selectShowTimes(@RequestBody ShowTimeRequestDTO showTimeRequestDTO) {
        return showtimeService.findShowtimeByDateAndMovieNameAndTheater(
                showTimeRequestDTO.getMovieName(),
                showTimeRequestDTO.getDate(),
                showTimeRequestDTO.getTheaterName());
    }

    @ResponseBody
    @PostMapping("/api/seats/selection")
    public String saveSeat(@RequestBody Map<String, String> payload, Model model) {
//        String date = payload.get("date");
//        String movieName = payload.get("movie");
//        String theaterName = payload.get("theater");
//        String screenName = payload.get("screen");
        //이 부분에 예매중으로 등록 해야 함. 좌석 선택부터 갈 수 있도록
        return "ex)예매중";
    }

    /**
     *  좌석 선택 페이지
     */
    @GetMapping("/seatSelection")
    public String seatSelection(@RequestParam("showtimeId") Long showtimeId, Model model) {
        Showtime showtime = showtimeService.findById(Long.valueOf(showtimeId))
                .orElseThrow(() -> new RuntimeException("쇼타임 아이디 없음: " + showtimeId)); //에러 만들어 줘야 함.

//        Screen screen = screenService.findById(showtime.getScreen().getId()).orElseThrow(() -> new RuntimeException("스크린 정보를 찾을 수 없습니다:" + showtime.getScreen().getId()));

        Screen screen = showtime.getScreen(); //스크린 가져오기
        List<Seat> seats = showtime.getScreen().getSeats(); //Seat 리스트 가져오기.
        List<Seat> sortedSeats = seats.stream() //가져온 좌석들 정렬하기
                .sorted(Comparator.comparingInt(Seat::getSeatLow)
                        .thenComparingInt(Seat::getSeatCol))
                .collect(Collectors.toList());

        //최대 행 계산
        int maxRow = sortedSeats.stream()
                .mapToInt(Seat::getSeatLow) // seatLow 값 돌면서 추출
                .max() // 가장 큰 값 찾기
                .orElse(0);

        // rows를 알파벳 리스트로 변환 ["A", "B", "C", ...]
        List<String> rowLabels  = IntStream.rangeClosed(0, maxRow - 1)
                .mapToObj(i -> String.valueOf((char) ('A' + i))) // 0부터 시작하는 숫자를 A, B, C로 변환
                .collect(Collectors.toList());

        // Map<String, List<Seat>> 구조로 변환
        Map<String, List<Seat>> seatMap = rowLabels .stream() //rowLabels(A, B, C, ...) 리스트를 스트림 형태로 순회합니다.
                .collect(Collectors.toMap(
                        row -> row, //키는 각 행 이름(A, B, C..
                        row -> seats.stream()
                                .filter(seat -> seat.getSeatLow() == row.charAt(0) - 'A' + 1)  ////예: seatLow = 1인 좌석은 row = "A"와 매칭됩니다.
                                .collect(Collectors.toList())
                ));


        model.addAttribute("seats", sortedSeats);
        model.addAttribute("screen", screen);
        model.addAttribute("showtime", showtime);
        model.addAttribute("seatMap", seatMap);

        return "reservation/seatSelection";
    }

    @ResponseBody
    @PostMapping("/reserve-seats")
    public ResponseEntity<ReservationResponseDTO> reserveSeats(@RequestBody ReserveSeatsRequestDTO requestDTO){
        List<String> seats = requestDTO.getSeats(); // "seats" 키에 저장된 값 가져오기
        Long showtimeId = requestDTO.getShowtimeId();

        log.info("reserve-seats: {}", seats);
        log.info("showtimeId: {}", showtimeId);

        for (String seat : seats) {
            String[] split = seat.split("-");
            log.info("seat: {}, {}", split[0], split[1]);
        }

        //member id는 public static Long getCurrentMemberId() 이런식으로 가져오자.
        Long memberId = 1L;
        ReservationResponseDTO responseDTO = reservationService.reservation(seats, memberId, showtimeId);

        log.info("MemberName = {}" , responseDTO.getMemberName());
        log.info("ReservationId = {}" , responseDTO.getReservationId());
        log.info("TotalPrice = {}" , responseDTO.getTotalPrice());
        log.info("MemberId = {}" , responseDTO.getMemberId());

        return ResponseEntity.ok(responseDTO);
    }

    /**
     *  결제 취소 test - ok
     */
    @ResponseBody
    @GetMapping("/cancel/{cancelId}")
    public String cancel(@PathVariable("cancelId") Long cancelId) {
        reservationService.reservationCancel(cancelId);
        return "cancel";
    }
}
