package com.example.letmovie.domain.movie.service;

import com.example.letmovie.domain.movie.entity.Movie;
import com.example.letmovie.domain.movie.entity.Status;
import com.example.letmovie.domain.movie.repository.MovieJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class MovieServiceImpl {

    private final MovieJpaRepository movieJpaRepository;

    public MovieServiceImpl(MovieJpaRepository movieJpaRepository) {
        this.movieJpaRepository = movieJpaRepository;
    }

    public List<Movie> getAllMovies() {
        return movieJpaRepository.findAll();
    }

    public Movie getMovieById(int movieId) {
        return movieJpaRepository.findById(movieId).orElse(null);
    }

    // 검색 기능
    public List<Movie> searchMoviesByName(String query) {
//        return movieJpaRepository.findByMovieNameStartingWithIgnoreCase(query);
        return movieJpaRepository.findByMovieNameContainingIgnoreCase(query);
    }

    // 카테고리
    public List<Movie> getMoviesByStatus(String status) {
        return movieJpaRepository.findByStatus(Status.valueOf(status));
    }

    public Page<Movie> getPagedMovies(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return movieJpaRepository.findAll(pageable);
    }

    // 카테고리 + 페이징
    public Page<Movie> getMoviesByStatus(String status, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return movieJpaRepository.findByStatus(Status.valueOf(status), pageable);
    }
}
