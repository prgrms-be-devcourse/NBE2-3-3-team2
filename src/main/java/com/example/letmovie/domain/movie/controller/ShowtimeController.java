package com.example.letmovie.domain.movie.controller;

import com.example.letmovie.domain.movie.dto.ShowtimeDTO;
import com.example.letmovie.domain.movie.service.ShowtimeServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/movie")
public class ShowtimeController {

    private final ShowtimeServiceImpl showtimeService;

    @GetMapping("/showtime")
    public String getShowtime(Model model) {
        List<ShowtimeDTO> showtime = showtimeService.getAllShowtime();

        showtime.add(new ShowtimeDTO(1, "이처럼 사소한 것들", "1관", 40, 12, "2024-12-19", "18:05"));
        showtime.add(new ShowtimeDTO(1, "이처럼 사소한 것들", "2관", 40, 10, "2024-12-19", "18:05"));
        showtime.add(new ShowtimeDTO(1, "이처럼 사소한 것들", "3관", 40, 40, "2024-12-19", "18:05"));
        showtime.add(new ShowtimeDTO(1, "이처럼 사소한 것들", "4관", 40, 0, "2024-12-19", "18:05"));

        model.addAttribute("showtime", showtime);

        return "showtime";
    }
}