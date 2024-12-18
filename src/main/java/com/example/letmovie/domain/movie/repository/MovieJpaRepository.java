package com.example.letmovie.domain.movie.repository;

import com.example.letmovie.domain.movie.entity.Movie;
import com.example.letmovie.domain.movie.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieJpaRepository extends JpaRepository<Movie, Integer> {

    // 검색 기능(포함될 시)
    List<Movie> findByMovieNameContainingIgnoreCase(String movieName);

    // 검색 기능(처음부터 비교)
    List<Movie> findByMovieNameStartingWithIgnoreCase(String movieName);

    // 카테고리 별로 출력을 위해 Status 에 따라 가져오기
    List<Movie> findByStatus(Status status);
}
