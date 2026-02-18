package com.example.ticketflow.interfaces.scheduler;

import com.example.ticketflow.application.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component // 스프링 빈 등록
@RequiredArgsConstructor
public class ReservationScheduler {

    private final ReservationService reservationService;

    // 10분마다 실행
    @Scheduled(fixedRate = 600000)
    public void processExpiredReservations() {
        int count = reservationService.expireOverdueReservations();
        if (count > 0) {
            log.info("만료된 예약 {}건 처리 완료.", count);
        }
    }
}
