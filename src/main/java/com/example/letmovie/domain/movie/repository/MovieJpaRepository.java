package com.example.letmovie.domain.movie.repository;

import com.example.letmovie.domain.movie.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieJpaRepository extends JpaRepository<Movie, Integer> {
}
