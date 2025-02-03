package com.example.letmovie.domain.movie.dto

import com.example.letmovie.domain.movie.entity.Movie
import lombok.AllArgsConstructor
import lombok.Getter

@Getter
@AllArgsConstructor
// MoviePageDTO.kt
data class MoviePageDTO(
    val content: List<Movie>,
    val currentPage: Int,
    val totalPages: Int
)