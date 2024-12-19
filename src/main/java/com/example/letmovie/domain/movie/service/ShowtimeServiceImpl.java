package com.example.letmovie.domain.movie.service;

import com.example.letmovie.domain.movie.dto.ShowtimeDTO;
import com.example.letmovie.domain.movie.repository.ShowtimeJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ShowtimeServiceImpl {

    private final ShowtimeJpaRepository showtimeJpaRepository;

    public List<ShowtimeDTO> getAllShowtime() {
        return showtimeJpaRepository.findAllShowtime();
    }
}
