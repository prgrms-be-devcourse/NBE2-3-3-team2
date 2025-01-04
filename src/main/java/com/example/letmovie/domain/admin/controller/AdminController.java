package com.example.letmovie.domain.admin.controller;

import com.example.letmovie.domain.admin.service.AdminServiceImpl;
import com.example.letmovie.domain.member.entity.Member;
import com.example.letmovie.domain.movie.dto.TheaterDTO;
import com.example.letmovie.domain.movie.entity.Movie;
import com.example.letmovie.domain.movie.entity.Theater;
import com.example.letmovie.domain.payment.entity.PaymentHistory;
import com.example.letmovie.domain.reservation.dto.ScreenDTO;
import com.example.letmovie.domain.reservation.entity.Screen;
import com.example.letmovie.domain.reservation.entity.Seat;
import com.example.letmovie.domain.reservation.entity.SeatType;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminServiceImpl adminService;


    // /admin/movie : 영화목록
    @GetMapping("/movie")
    public String movie(Model model) {
        List<Movie> movies = adminService.findAllMovies();
        /*for(Movie movie : movies) {
            System.out.println(movie);
        }*/
        model.addAttribute("movies", movies);
        return "admin_movie";
    }

    // /admin/movie/post : 영화 추가 첫화면
    @GetMapping("/movie/post")
    public String moviePost(Model model) {
        return "admin_movie_post";
    }

    // /admin/movie/post/search : 영화 추가 검색 이후 화면
    @GetMapping("/movie/post/search")
    public String moviePostSearch(@RequestParam("putmovieNm") String movieNm, Model model) {

        try {
            String movieCd = adminService.getMovieCodeByName(movieNm); // 영화 코드 가져오기
            if (movieCd != null) {
                model.addAttribute("movieDetails", adminService.getMovieInfoByCode(movieCd)); // 상세 정보 가져오기
            } else {
                model.addAttribute("movieDetails", null); // 검색 결과가 없는 경우
                model.addAttribute("error", "검색 결과가 없습니다. 영화 제목을 정확히 기입해주세요.");
            }
        } catch (Exception e) {
            model.addAttribute("movieDetails", null); // 오류 시 기본값 처리
            model.addAttribute("error", "오류가 발생했습니다. 다시 시도해주세요.");
        }


        return "admin_movie_post_search";
    }

    // /admin/movie/post/add : 영화 추가 처리용
    @PostMapping("/movie/post/add")
    public String addMovie(@ModelAttribute Movie movie, Model model) {
        try {
            adminService.addMovie(movie); // 서비스 계층에서 데이터 저장 처리
            model.addAttribute("success", "영화가 성공적으로 추가되었습니다!");
        } catch (Exception e) {
            model.addAttribute("error", "영화 추가 중 문제가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/admin/movie"; // 영화 목록으로 리다이렉트
    }

    // /admin/movie/modify : 영화 수정 첫화면
    @GetMapping("/movie/modify")
    public String modifyMovie(Model model) {
        return "admin_movie_modify";
    }

    // /admin/movie/modify/search : 영화 수정 검색 이후 화면
    @GetMapping("/movie/modify/search")
    public String modifyMovieSearch(@RequestParam("movieId") Long movieId, Model model) {
        try {
            Movie movie = adminService.findMovieById(movieId);
            if (movie != null) {
                model.addAttribute("movie", movie);
            } else {
                model.addAttribute("error", "해당 ID에 해당하는 영화가 없습니다.");
            }
        } catch (Exception e) {
            model.addAttribute("error", "오류가 발생했습니다. 다시 시도해주세요.");
        }
        return "admin_movie_modify_search";
    }

    // /admin/movie/modify/ok : 영화 수정 처리용
    @PostMapping("/movie/modify/ok")
    public String modifyMovieOk(@ModelAttribute Movie movie, RedirectAttributes redirectAttributes) {
        try {
            adminService.updateMovie(movie);
            redirectAttributes.addFlashAttribute("success", "영화 정보가 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "오류가 발생했습니다. 수정에 실패했습니다.");
        }
        return "redirect:/admin/movie"; // 영화 목록으로 리다이렉트
    }

    // /admin/movie/delete : 영화 삭제 첫화면
    @GetMapping("/movie/delete")
    public String deleteMovie(Model model) {
        return "admin_movie_delete";
    }

    @GetMapping("/movie/delete/search")
    public String deleteMovieSearch(@RequestParam("movieId") Long movieId, Model model, RedirectAttributes redirectAttributes) {
        Movie movie = adminService.findMovieById(movieId);
        if (movie != null) {
            model.addAttribute("movie", movie);
            return "admin_movie_delete_search";
        } else {
            redirectAttributes.addFlashAttribute("error", "해당 ID에 해당하는 영화가 없습니다.");
            return "redirect:/admin/movie/delete";
        }
    }

    @PostMapping("/movie/delete/ok")
    public String deleteMovieOk(@RequestParam("movieId") Long movieId, RedirectAttributes redirectAttributes) {
        try {
            adminService.deleteMovieById(movieId);
            redirectAttributes.addFlashAttribute("success", "영화가 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "영화 삭제 중 오류가 발생했습니다.");
        }
        return "redirect:/admin/movie"; // 영화 목록으로 리다이렉트
    }

    //영화관
    @GetMapping("/theater")
    public String theater(Model model) {
        model.addAttribute("theaters", adminService.findAllTheaters());
        return "admin_theater";
    }

    @GetMapping("/theater/post")
    public String theaterPost(Model model) {
        model.addAttribute("theater", Theater.builder().build());
        return "admin_theater_post";
    }

    @PostMapping("/theater/post")
    public String addTheater(@ModelAttribute Theater theater, RedirectAttributes redirectAttributes) {
        try {
            adminService.addTheater(theater);
            redirectAttributes.addFlashAttribute("success", "영화관이 성공적으로 추가되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "영화관 추가 중 오류가 발생했습니다.");
        }
        return "redirect:/admin/theater";
    }

    @GetMapping("/theater/modify/{id}")
    public String modifyTheater(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Theater theater = adminService.findTheaterById(id);
            model.addAttribute("theater", theater);
            return "admin_theater_modify"; // 극장 수정 뷰 이름
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "해당 극장을 찾을 수 없습니다.");
            return "redirect:/admin/theater";
        }
    }

    @PostMapping("/theater/modify")
    public String modifyTheater(@ModelAttribute TheaterDTO theaterDto, RedirectAttributes redirectAttributes) {
        //System.out.println("Received Theater: " + theater.getId() + ", " + theater.getTheaterName());
        try {
            adminService.updateTheater(theaterDto);
            redirectAttributes.addFlashAttribute("success", "극장이 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "극장 수정 중 오류가 발생했습니다.");
        }
        return "redirect:/admin/theater";
    }

    @PostMapping("/theater/delete/{id}")
    public String deleteTheater(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.deleteTheaterById(id);
            redirectAttributes.addFlashAttribute("success", "극장이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "극장 삭제 중 오류가 발생했습니다.");
        }
        return "redirect:/admin/theater";
    }

    //상영관
    @GetMapping("/screen")
    public String screenManagement(Model model) {
        List<Screen> screens = adminService.findAllScreens();
        List<Theater> theaters = adminService.findAllTheaters();

        model.addAttribute("screens", screens);
        model.addAttribute("theaters", theaters);
        return "admin_screen";
    }

    @PostMapping("/screen/add")
    public String addScreen(@ModelAttribute ScreenDTO screenDTO, RedirectAttributes redirectAttributes) {
        try {
            adminService.addScreen(screenDTO);
            redirectAttributes.addFlashAttribute("success", "상영관이 성공적으로 추가되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "상영관 추가 중 문제가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/admin/screen";
    }

    @GetMapping("/screen/modify")
    public String modifyScreen(@RequestParam("id") Long screenId, Model model) {
        Screen screen = adminService.findScreenById(screenId);
        if (screen != null) {
            ScreenDTO screenDTO = new ScreenDTO(screen.getId(), screen.getScreenName(), screen.getTheater().getId());
            model.addAttribute("screen", screenDTO);
        } else {
            model.addAttribute("error", "상영관을 찾을 수 없습니다.");
        }
        return "admin_screen_modify";
    }

    @PostMapping("/screen/modify")
    public String modifyScreenSubmit(@ModelAttribute ScreenDTO screenDTO, RedirectAttributes redirectAttributes) {
        try {
            System.out.println("Received Screen ID: " + screenDTO.getId()); // Debugging 출력
            adminService.updateScreen(screenDTO);
            redirectAttributes.addFlashAttribute("success", "상영관 정보가 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "상영관 수정 중 문제가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/admin/screen";
    }

    @PostMapping("/screen/delete")
    public String deleteScreen(@RequestParam("id") Long screenId, RedirectAttributes redirectAttributes) {
        try {
            adminService.deleteScreen(screenId);
            redirectAttributes.addFlashAttribute("success", "상영관이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "상영관 삭제 중 문제가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/admin/screen";
    }

    // 좌석 메인페이지 (상영관을 보여줌)
    @GetMapping("/seat")
    public String seat(Model model) {
        model.addAttribute("screens", adminService.getAllScreens());
        return "admin_seat";
    }

    // 좌석추가폼
    @GetMapping("/seat/{screenId}")
    public String addSeatsForm(@PathVariable Long screenId, Model model) {
        model.addAttribute("screenId", screenId);
        return "admin_seat_post";
    }

    // 좌석 추가 처리
    @PostMapping("/seat/{screenId}/add")
    public String addSeats(@PathVariable Long screenId, @RequestParam int seatLow, @RequestParam int seatCol) {
        adminService.addSeatsToScreen(screenId, seatLow, seatCol);
        return "redirect:/admin/seat";
    }

    // 좌석수정 메인페이지 (해당하는 상영관이 가지는 좌석들 테이블을 모두 보여줌) "/seat/edit/{screenId}"
    @GetMapping("/seat/edit/{screenId}")
    public String editSeats(@PathVariable Long screenId, Model model) {
        model.addAttribute("seats", adminService.getSeatsByScreenId(screenId));
        model.addAttribute("screenId", screenId);
        return "admin_seat_list";
    }

    // 좌석 수정 페이지 접근
    @GetMapping("/seat/edit/seat/{seatId}")
    public String editSeatForm(@PathVariable Long seatId, Model model) {
        Seat seat = adminService.getSeatById(seatId);
        model.addAttribute("seat", seat);
        model.addAttribute("seatTypes", SeatType.values()); // Enum 타입 설정
        return "admin_seat_edit";
    }

    @PostMapping("/seat/edit/update")
    public String updateSeat(@RequestParam Long seatId, @RequestParam String seatType, @RequestParam int price) {
        adminService.updateSeat(seatId, SeatType.valueOf(seatType), price);
        return "redirect:/admin/seat";
    }

    @PostMapping("/seat/delete/all/{screenId}")
    public String deleteAllSeats(@PathVariable Long screenId, RedirectAttributes redirectAttributes) {
        try {
            adminService.deleteAllSeatsByScreenId(screenId);
            redirectAttributes.addFlashAttribute("message", "All seats for Screen ID " + screenId + " have been deleted.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete seats: " + e.getMessage());
        }
        return "redirect:/admin/seat";
    }

    // 상영시간대
    @GetMapping("/showtime")
    public String showtime(Model model) {
        model.addAttribute("showtimes", adminService.getAllShowtimes());

        // Add additional attributes for screen and movie names
        model.addAttribute("screenNames", adminService.getAllScreenNames()); // ScreenService에서 스크린 이름 가져오기
        model.addAttribute("movieNames", adminService.getAllMovieNames()); // MovieService에서 영화 이름 가져오기

        return "admin_showtime";
    }

    @GetMapping("/showtime/add")
    public String addShowtimeForm(Model model) {
        model.addAttribute("screens", adminService.getAllScreens());
        model.addAttribute("movies", adminService.findAllMovies());
        return "admin_showtime_add";
    }

    @PostMapping("/showtime/add")
    public String addShowtime(@RequestParam Long screenId,
                              @RequestParam Long movieId,
                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate showtimeDate,
                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime showtimeTime,
                              RedirectAttributes redirectAttributes) {

        // 남은 좌석 수 계산
        //int remainingSeats = adminService.countAvailableSeatsByScreenId(screenId);
        int totalSeats = adminService.countAvailableSeatsByScreenId(screenId); // 스크린 ID로부터 총 좌석 수 계산
        int remainingSeats = totalSeats; // 남은 좌석 수 초기값

        adminService.addShowtime(screenId, movieId, showtimeDate, showtimeTime, totalSeats, remainingSeats);

        redirectAttributes.addFlashAttribute("message", "Showtime added successfully!");
        return "redirect:/admin/showtime";
    }

    /*@GetMapping("/showtime/edit/{id}")
    public String editShowtimeForm(@PathVariable Long id, Model model) {
        Showtime showtime = adminService.getShowtimeById(id); // Showtime 엔터티 가져오기
        model.addAttribute("showtime", showtime);
        model.addAttribute("screens", adminService.getAllScreens());
        model.addAttribute("movies", adminService.findAllMovies());
        return "admin_showtime_edit";
    }*/

    /*@PostMapping("/showtime/edit/update")
    @Transactional
    public String updateShowtime(@ModelAttribute Showtime showtime, RedirectAttributes redirectAttributes) {
        // Optionally recalculate remainingSeats if needed (for modifications)
        adminService.updateShowtime(showtime);

        redirectAttributes.addFlashAttribute("message", "Showtime updated successfully!");
        return "redirect:/admin/showtime";
    }*/




    @GetMapping("/showtime/delete/{id}")
    @Transactional
    public String deleteShowtime(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        adminService.deleteShowtime(id);
        redirectAttributes.addFlashAttribute("message", "Showtime deleted successfully!");
        return "redirect:/admin/showtime";
    }



    // /admin/member : 회원정보
    @GetMapping("/member")
    public String member() {
        return "admin_member";
    }

    // /admin/member/search : 회원정보검색후
    @GetMapping("/member/search")
    public String memberSearch(@RequestParam("inputnickname") String nickname, Model model) {

        try {
            List<Member> members = adminService.findMemberByName(nickname); // 닉네임으로 검색
            if(members != null) {
                model.addAttribute("members", members);
            } else {
                model.addAttribute("error", "검색 결과가 없습니다. 검색어를 확인해주세요.");
            }
        } catch (Exception e) {
            model.addAttribute("error", "오류가 발생했습니다. 다시 시도해주세요.");
        }

        return "admin_member_search";
    }

    // /admin/member/modify : ID로 특정 회원 조회
    @GetMapping("/member/modify")
    public String modifyMemberById(@RequestParam("id") Long memberId, Model model) {
        System.out.println("modifyMemberById called with memberId: " + memberId);
        try {
            Member member = adminService.findMemberById(memberId);
            if (member != null) {
                model.addAttribute("member", member);
            } else {
                model.addAttribute("error", "해당 ID에 해당하는 회원이 없습니다.");
            }
        } catch (Exception e) {
            model.addAttribute("error", "오류가 발생했습니다. 다시 시도해주세요.");
        }
        return "admin_member_modify";
    }

    // /admin/member/modify/ok : 회원 수정 처리
    @PostMapping("/member/modify/ok")
    public String modifyMemberOk(@ModelAttribute Member member, RedirectAttributes redirectAttributes) {
        try {
            adminService.updateMember(member);
            redirectAttributes.addFlashAttribute("success", "회원 정보가 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "오류가 발생했습니다. 수정에 실패했습니다.");
        }
        return "redirect:/admin/member";
    }

    // 회원 결제 관리 메인페이지
    @GetMapping("/payment")
    public String payment() {
        return "admin_payment";
    }

    // 회원 닉네임 검색
    @GetMapping("/payment/membersearch")
    public String paymentMemberSearch(@RequestParam("inputnickname") String nickname, Model model) {
        /*List<Member> members = adminService.findMemberByName(nickname);
        model.addAttribute("members", members);*/
        try {
            List<Member> members = adminService.findMemberByName(nickname); // 닉네임으로 검색
            if(members != null) {
                model.addAttribute("members", members);
            } else {
                model.addAttribute("error", "검색 결과가 없습니다. 검색어를 확인해주세요.");
            }
        } catch (Exception e) {
            model.addAttribute("error", "오류가 발생했습니다. 다시 시도해주세요.");
        }
        return "admin_payment_membersearch";
    }

    // 회원 결제내역 조회
    @GetMapping("/payment/{memberId}")
    public String viewMemberPayments(@PathVariable("memberId") Long memberId, Model model) {
        List<PaymentHistory> paymentHistories = adminService.findPaymentHistoryByMemberId(memberId);
        model.addAttribute("paymentHistories", paymentHistories);
        return "admin_payment_memberpaymenthistory";
    }

}
