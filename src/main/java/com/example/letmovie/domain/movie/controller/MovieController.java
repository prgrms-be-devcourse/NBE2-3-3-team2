package com.example.letmovie.domain.movie.controller;

import com.example.letmovie.domain.movie.entity.Movie;
import com.example.letmovie.domain.movie.service.MovieServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MovieController {

    private final MovieServiceImpl movieService;

    // home page
    @GetMapping("/")
    public String homePage(Model model) {

        List<Movie> movies = movieService.getAllMovies();
        model.addAttribute("movies", movies);

        return "home";
    }

    // 영화 상세 페이지
    @GetMapping("/movie/{movieId}")
    public String movieDetail(@PathVariable int movieId, Model model) {

        Movie movie = movieService.getMovieById(movieId);
        model.addAttribute("movie", movie);

        return "movie_detail";
    }

    // 영화 상세 페이지
    @GetMapping("/movie/total_movie")
    public String totalMovie(Model model) {

        List<Movie> movies = movieService.getAllMovies();
        model.addAttribute("movies", movies);

        return "total_movie";
    }
}
