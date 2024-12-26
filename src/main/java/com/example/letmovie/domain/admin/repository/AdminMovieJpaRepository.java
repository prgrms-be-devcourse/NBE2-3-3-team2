package com.example.letmovie.domain.admin.repository;

import com.example.letmovie.domain.movie.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AdminMovieJpaRepository extends JpaRepository<Movie, Long> {
    @Query(value = "select m from Movie m")
    List<Movie> findAllMovies();
}
