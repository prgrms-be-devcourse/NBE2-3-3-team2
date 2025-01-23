package com.example.letmovie.domain.movie.controller;

import com.example.letmovie.domain.movie.dto.ReviewDTO;
import com.example.letmovie.domain.movie.entity.Movie;
import com.example.letmovie.domain.movie.service.MovieServiceImpl;
import com.example.letmovie.domain.movie.service.ReviewServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MovieController {

    private final MovieServiceImpl movieService;
    private final ReviewServiceImpl reviewService;

    // home page
    @GetMapping({"/", "/private"})
    public String homePage(Model model) {

        long startTime = System.currentTimeMillis(); // 시작 시간

        List<Movie> movies = movieService.getAllMovies();
        model.addAttribute("movies", movies);

        long endTime = System.currentTimeMillis(); // 종료 시간

        log.info("전체 영화 로딩 - time : {} ms", (endTime - startTime));

        return "home";
    }

    // 영화 상세 페이지
    @GetMapping({"/movie/{movieId}", "private/movie/{movieId}"})
    public String movieDetail(@PathVariable int movieId, Model model) {

        Movie movie = movieService.getMovieById(movieId);
        List<ReviewDTO> reviews = reviewService.getReviewsByMovieId(movieId); // 해당 영화의 리뷰 목록 가져오기
        model.addAttribute("movie", movie);
        model.addAttribute("reviews", reviews);

        return "movie_detail";
    }

    // 영화 카테고리별 + 페이징(전체 영화 카테고리 에서만)
    @GetMapping({"/movies", "private/movies"})
    public String moviesByCategory(
            @RequestParam(defaultValue = "ALL") String category,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "1") int page, // 페이지 번호
            @RequestParam(defaultValue = "20") int size, // 페이지 크기
            Model model) {

        long startTime = System.currentTimeMillis(); // 시작 시간 측정

        if (query != null && !query.isEmpty()) {
            // 검색어가 있을 경우 이름으로 검색
            List<Movie> movies = movieService.searchMoviesByName(query);
            model.addAttribute("movies", movies);
            model.addAttribute("query", query); // 검색어 전달

            long endTime = System.currentTimeMillis(); // 종료 시간 측정
            log.info("영화 검색(전체 영화) - 검색어: {}, 소요 시간: {} ms", query, (endTime - startTime));

        } else {
            // 카테고리별 영화 필터링
            Page<Movie> moviePage;
            switch (category.toUpperCase()) {
                case "RECOMMEND":
                    moviePage = movieService.getMoviesByStatus("RECOMMEND", page, size);
                    break;
                case "PREV":
                    moviePage = movieService.getMoviesByStatus("PREV", page, size);
                    break;
                default:
                    moviePage = movieService.getPagedMovies(page, size);
            }

            model.addAttribute("movies", moviePage.getContent());
            model.addAttribute("totalPages", moviePage.getTotalPages());
            model.addAttribute("page", page);

            long endTime = System.currentTimeMillis(); // 종료 시간 측정
            log.info("영화 검색(전체 영화) - 검색어: {}, time: {} ms", query, (endTime - startTime));
        }

        model.addAttribute("category", category.toUpperCase());
        return "total_movie";
    }

}
