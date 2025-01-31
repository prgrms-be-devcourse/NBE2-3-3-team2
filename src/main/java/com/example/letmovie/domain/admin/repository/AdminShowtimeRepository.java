package com.example.letmovie.domain.admin.repository;

import com.example.letmovie.domain.movie.entity.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminShowtimeRepository extends JpaRepository<Showtime, Long> {
    List<Showtime> findByScreenId(Long screenId);
    List<Showtime> findByMovieId(Long movieId);
    boolean existsByScreenId(Long screenId); // 특정 screenId에 showtime이 존재하는지 확인
}
