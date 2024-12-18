package com.example.letmovie.domain.movie.repository;

import com.example.letmovie.domain.movie.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieJpaRepository extends JpaRepository<Movie, Integer> {

    // 검색 기능(포함될 시)
    List<Movie> findByMovieNameContainingIgnoreCase(String movieName);

    // 검색 기능(처음부터 비교)
    List<Movie> findByMovieNameStartingWithIgnoreCase(String movieName);
}
