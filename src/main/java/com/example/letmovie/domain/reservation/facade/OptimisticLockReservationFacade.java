package com.example.letmovie.domain.reservation.facade;

import com.example.letmovie.domain.reservation.dto.response.ReservationResponseDTO;
import com.example.letmovie.domain.reservation.service.lock.OptimisticLockReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OptimisticLockReservationFacade {
    private final OptimisticLockReservationService optimisticLockReservationService;

    /**
     * 시도횟수는 맞음 300개임 -> 100명에 3번씩
     *
     */

    public void reservation(List<String> seatList, Long memberId, Long showtimeId) throws InterruptedException {
        int maxRetries = 2; // 최대 재시도 횟수
        int attempt = 0;

        while (attempt < maxRetries) {
            try {
                optimisticLockReservationService.reservation(seatList, memberId, showtimeId);
                return;
            } catch (Exception e) {
                attempt++;
                log.info("시도 횟수 = {} : ",attempt);
                if (attempt >= maxRetries) {
                    throw new RuntimeException("최대 재시도 횟수를 초과했습니다. : " + e.getMessage());
                }
                Thread.sleep(50); // 재시도 전 대기
            }
        }
        throw new RuntimeException("예약에 실패했습니다."); // 비정상 상황에 대한 처리
    }
}
