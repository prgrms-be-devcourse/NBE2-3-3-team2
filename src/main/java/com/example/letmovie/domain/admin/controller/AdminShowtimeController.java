package com.example.letmovie.domain.admin.controller;

import com.example.letmovie.domain.admin.service.AdminServiceImpl;
import com.example.letmovie.domain.admin.service.AdminShowtimeServiceImpl;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminShowtimeController {
    @Autowired
    private AdminShowtimeServiceImpl adminService;
    // 상영시간대
    @GetMapping("/showtime")
    public String showtime(Model model) {
        model.addAttribute("showtimes", adminService.getAllShowtimes());

        // 수정된 방식으로 screenNames 가져오기
        Map<Long, String> screenNamesMap = adminService.getAllScreenNamesById(); // screenId 기반 맵 생성
        model.addAttribute("screenNames", screenNamesMap); // ID로 접근하여 이름 가져오기

        //model.addAttribute("movieNames", adminService.getAllMovieNames()); // MovieService에서 영화 이름 가져오기
        Map<Long,String> movieNamesMap = adminService.getAllMovieNamesById();
        model.addAttribute("movieNames", movieNamesMap);

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

    @GetMapping("/showtime/delete/{id}")
    @Transactional
    public String deleteShowtime(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        adminService.deleteShowtime(id);
        redirectAttributes.addFlashAttribute("message", "Showtime deleted successfully!");
        return "redirect:/admin/showtime";
    }
}
