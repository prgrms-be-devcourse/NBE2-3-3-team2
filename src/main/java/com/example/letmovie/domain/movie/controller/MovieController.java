package com.example.letmovie.domain.movie.controller;

import com.example.letmovie.domain.movie.entity.Movie;
import com.example.letmovie.domain.movie.service.MovieServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

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


    // 전체 영화 페이지
    @GetMapping("/movie/total_movie")
    public String totalMovie(@RequestParam(required = false) String query, Model model) {
        if (query != null && !query.isEmpty()) {
            List<Movie> movies = movieService.searchMoviesByName(query);
            model.addAttribute("movies", movies);
        } else {
            List<Movie> movies = movieService.getAllMovies();
            model.addAttribute("movies", movies);
        }
        return "total_movie";
    }

//    // 카테고리별 영화 페이지
//    @GetMapping("/movies")
//    public String moviesByCategory(@RequestParam(defaultValue = "ALL") String category, Model model) {
//        List<Movie> movies;
//
//        // 카테고리별 영화 필터링
//        switch (category.toUpperCase()) {
//            case "RECOMMEND":
//                movies = movieService.getMoviesByStatus("RECOMMEND");
//                break;
//            case "PREV":
//                movies = movieService.getMoviesByStatus("PREV");
//                break;
//            default: // "ALL"
//                movies = movieService.getAllMovies();
//        }
//
//        model.addAttribute("movies", movies);
//        model.addAttribute("category", category.toUpperCase()); // 현재 카테고리
//        return "total_movie";
//    }

    @GetMapping("/movies")
    public String moviesByCategory(@RequestParam(defaultValue = "ALL") String category,
                                   @RequestParam(required = false) String query, Model model) {
        List<Movie> movies;

        if (query != null && !query.isEmpty()) {
            // 검색어가 있을 경우 이름으로 검색
            movies = movieService.searchMoviesByName(query);
            model.addAttribute("query", query); // 검색어 전달
        } else {
            // 카테고리별 영화 필터링
            switch (category.toUpperCase()) {
                case "RECOMMEND":
                    movies = movieService.getMoviesByStatus("RECOMMEND");
                    break;
                case "PREV":
                    movies = movieService.getMoviesByStatus("PREV");
                    break;
                default:
                    movies = movieService.getAllMovies();
            }
        }

        model.addAttribute("movies", movies);
        model.addAttribute("category", category.toUpperCase());
        return "total_movie";
    }
}
