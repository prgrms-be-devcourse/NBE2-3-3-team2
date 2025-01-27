package com.example.letmovie.domain.movie.service;

import com.example.letmovie.domain.movie.entity.Movie;
import com.example.letmovie.domain.movie.entity.Status;
import com.example.letmovie.domain.movie.repository.MovieJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
public class MovieServiceImpl {

    private final MovieJpaRepository movieJpaRepository;

    public MovieServiceImpl(MovieJpaRepository movieJpaRepository) {
        this.movieJpaRepository = movieJpaRepository;
    }

    @Cacheable(value = "all_movies", key = "'allMovies'")
    public List<Movie> getAllMovies() {
        return movieJpaRepository.findAll();
    }

    public Movie getMovieById(int movieId) {
        return movieJpaRepository.findById(movieId).orElse(null);
    }

    // 검색 기능
    @Cacheable(value = "movies", key = "#query") // Redis 캐싱 추가
    public List<Movie> searchMoviesByName(String query) {

        //long startTime = System.currentTimeMillis(); // 시작 시간

        List<Movie> movies = movieJpaRepository.findByMovieNameContainingIgnoreCase(query);
//        List<Movie> movies = movieJpaRepository.findByMovieNameStartingWithIgnoreCase(query);
        // full Text 인덱스
//        List<Movie> movies = movieJpaRepository.searchMoviesByNameFullText(query);
        //long endTime = System.currentTimeMillis(); // 종료 시간

        //log.info("영화 검색(연관 검색) - 검색어: {}, time : {} ms", query, (endTime - startTime));
        return movies;
    }

    // 카테고리
    public List<Movie> getMoviesByStatus(String status) {
        return movieJpaRepository.findByStatus(Status.valueOf(status));
    }

//    @Cacheable("movies")
    public Page<Movie> getPagedMovies(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return movieJpaRepository.findAll(pageable);
    }

    // 카테고리 + 페이징
    @Cacheable(value = "movies_by_status", key = "#status + '-' + #page + '-' + #size")
    public Page<Movie> getMoviesByStatus(String status, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return movieJpaRepository.findByStatus(Status.valueOf(status), pageable);
    }
}
