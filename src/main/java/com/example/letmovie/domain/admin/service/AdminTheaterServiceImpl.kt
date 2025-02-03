package com.example.letmovie.domain.admin.service

import com.example.letmovie.domain.admin.repository.AdminTheaterRepository
import com.example.letmovie.domain.movie.dto.TheaterDTO
import com.example.letmovie.domain.movie.entity.Theater
import org.springframework.stereotype.Service

@Service
class AdminTheaterServiceImpl (
    private val adminTheaterRepository: AdminTheaterRepository
){
    // 영화관 목록 조회
    fun findAllTheaters(): List<Theater> = adminTheaterRepository.findAll()

    // ID로 특정 극장 조회
    fun findTheaterById(id: Long): Theater = adminTheaterRepository.findById(id)
        .orElseThrow { IllegalArgumentException("극장을 찾을 수 없습니다. ID: $id") }

    // 극장 추가
    fun addTheater(theater: Theater) {
        adminTheaterRepository.save(theater)
    }

    // 극장 수정
    fun updateTheater(theaterDto: TheaterDTO) {
        val existingTheater = adminTheaterRepository.findById(theaterDto.id)
            .orElseThrow { IllegalArgumentException("극장을 찾을 수 없습니다. ID: " + theaterDto.id) }
        existingTheater.setTheaterName(theaterDto.theaterName)
        adminTheaterRepository.save(existingTheater)
    }

    // 극장 삭제
    fun deleteTheaterById(id: Long) {
        adminTheaterRepository.deleteById(id)
    }
}
