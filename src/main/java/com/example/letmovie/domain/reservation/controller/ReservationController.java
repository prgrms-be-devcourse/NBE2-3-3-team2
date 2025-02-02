package com.example.letmovie.domain.reservation.controller;

import com.example.letmovie.domain.auth.util.SecurityUtil;
import com.example.letmovie.domain.member.entity.Member;
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
import com.example.letmovie.domain.reservation.facade.OptimisticLockReservationFacade;
import com.example.letmovie.domain.reservation.service.ReservationService;
import com.example.letmovie.domain.reservation.service.ShowtimeService;
import com.example.letmovie.global.exception.exceptionClass.auth.MemberNotFoundException;
import com.example.letmovie.global.exception.exceptionClass.reservation.ShowtimeNotFoundException;
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

@Slf4j
@Controller
@RequiredArgsConstructor
public class ReservationController implements ReservationControllerDocs{

    private final ShowtimeService showtimeService;
    private final ReservationService reservationService;
    private final OptimisticLockReservationFacade optimisticLockReservationFacade;

    @GetMapping("/reservation")
    public String reservation() {
        return "reservation/reservationHome";
    }

    @ResponseBody
    @PostMapping("/api/dates")
    public MovieNamesResponseDTO selectDate(@RequestBody DateRequestDTO selectDateDTO) {
        String selectedDate = selectDateDTO.getDate(); //selectedDate = ex)2024-12-18
        log.info("selectDate:{}", selectedDate);
        return showtimeService.findMovieNameByDate(selectedDate);
    }

    @ResponseBody
    @PostMapping("/api/movies")
    public List<TheaterResponseDTO> selectTheater(@RequestBody TheaterRequestDTO theaterRequestDTO) {
        return showtimeService.findTheatersByMovieNameAndDate(
                theaterRequestDTO.getMovieName(),
                theaterRequestDTO.getDate()
        );
    }

    @ResponseBody
    @PostMapping("/api/showtimes")
    public List<ShowTimeResponseDTO> selectShowTimes(@RequestBody ShowTimeRequestDTO showTimeRequestDTO) {
        return showtimeService.findShowtimeByDateAndMovieNameAndTheater(
                showTimeRequestDTO.getMovieName(),
                showTimeRequestDTO.getDate(),
                showTimeRequestDTO.getTheaterName());
    }


    /**
     *  좌석 선택 페이지
     */
    @GetMapping("/seatSelection")
    public String seatSelection(@RequestParam("showtimeId") Long showtimeId, Model model) {
        Showtime showtime = showtimeService.findById(Long.valueOf(showtimeId))
                .orElseThrow(ShowtimeNotFoundException::new);

        Screen screen = showtime.getScreen(); //스크린 가져오기
        List<Seat> seats = showtime.getScreen().getSeats(); //Seat 리스트 가져오기.

        List<Seat> sortedSeats = seats.stream() //가져온 좌석들 정렬하기
                .sorted(Comparator.comparingInt(Seat::getSeatLow)
                        .thenComparingInt(Seat::getSeatCol))
                .collect(Collectors.toList());

        Map<String, List<Seat>> seatMap = showtimeService.convertSeatsToRowMap(sortedSeats);

        model.addAttribute("seats", sortedSeats);
        model.addAttribute("screen", screen);
        model.addAttribute("showtime", showtime);
        model.addAttribute("seatMap", seatMap);

        return "reservation/seatSelection";
    }

    @ResponseBody
    @PostMapping("/reserve-seats")
    public ResponseEntity<ReservationResponseDTO> reserveSeats(@RequestBody ReserveSeatsRequestDTO requestDTO) throws InterruptedException {
        List<String> seats = requestDTO.getSeats(); // "seats" 키에 저장된 값 가져오기
        Long showtimeId = requestDTO.getShowtimeId();

        Member member = SecurityUtil.getCurrentMember()
                .orElseThrow(MemberNotFoundException::new);

        ReservationResponseDTO responseDTO = optimisticLockReservationFacade.reservation(seats, member.getId(), showtimeId);

        return ResponseEntity.ok(responseDTO);
    }

    /**
     *  결제 취소 test - ok
     */
//    @ResponseBody
//    @GetMapping("/cancel/{cancelId}")
//    public String cancel(@PathVariable("cancelId") Long cancelId) {
//        reservationService.reservationCancel(cancelId);
//        return "cancel";
//    }

}
