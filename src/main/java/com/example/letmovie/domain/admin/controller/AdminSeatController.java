package com.example.letmovie.domain.admin.controller;

import com.example.letmovie.domain.admin.service.AdminSeatServiceImpl;
import com.example.letmovie.domain.reservation.entity.Seat;
import com.example.letmovie.domain.reservation.entity.SeatType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminSeatController {
    @Autowired
    private AdminSeatServiceImpl adminService;

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
}
