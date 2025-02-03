package com.example.letmovie.domain.admin.service

import com.example.letmovie.domain.admin.repository.AdminScreenRepository
import com.example.letmovie.domain.admin.repository.AdminTheaterRepository
import com.example.letmovie.domain.movie.entity.Theater
import com.example.letmovie.domain.reservation.dto.request.ScreenDTO
import com.example.letmovie.domain.reservation.entity.Screen
import org.springframework.stereotype.Service

@Service
class AdminScreenServiceImpl (
    private val adminTheaterRepository: AdminTheaterRepository,
    private val adminScreenRepository: AdminScreenRepository
){
    // 상영관 목록조회(영화관별 정렬)
    fun findAllScreensSorted(): List<Screen> {
        return adminScreenRepository.findAllByOrderByTheaterIdAscScreenNameAsc()
    }

    // 극장 목록 조회
    fun findAllTheaters(): List<Theater> {
        return adminTheaterRepository.findAll()
    }

    // ID조회
    fun findScreenById(screenId: Long): Screen {
        return adminScreenRepository.findById(screenId)
            .orElseThrow { IllegalArgumentException("상영관을 찾을 수 없습니다. ID: $screenId") }
    }

    // 상영관 추가
    fun addScreen(screenDTO: ScreenDTO) {
        val theater = adminTheaterRepository.findById(screenDTO.theaterId)
            .orElseThrow { IllegalArgumentException("영화관을 찾을 수 없습니다. ID: " + screenDTO.theaterId) }
        val screen = Screen(theater, ArrayList(),
            screenDTO.screenName ?: throw IllegalArgumentException("상영관 이름은 필수입니다.")
        )
        adminScreenRepository.save(screen)
    }

    // 상영관 수정
    fun updateScreen(screenDTO: ScreenDTO) {
        requireNotNull(screenDTO.id) { "Screen ID는 필수입니다." }

        val existingScreen = adminScreenRepository.findById(screenDTO.id)
            .orElseThrow { IllegalArgumentException("상영관을 찾을 수 없습니다. ID: " + screenDTO.id) }

        existingScreen.setScreenName(screenDTO.screenName ?: throw IllegalArgumentException("상영관 이름은 필수입니다."))

        adminScreenRepository.save(existingScreen)
    }

    // 상영관 삭제
    fun deleteScreen(screenId: Long) {
        require(adminScreenRepository.existsById(screenId)) { "상영관을 찾을 수 없습니다. ID: $screenId" }
        adminScreenRepository.deleteById(screenId)
    }
}
