package com.example.ticketflow.concurrency;

import com.example.ticketflow.application.ReservationService;
import com.example.ticketflow.application.dto.ReservationCreateCommand;
import com.example.ticketflow.domain.seat.Seat;
import com.example.ticketflow.domain.seat.SeatRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ReservationConcurrencyTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private SeatRepository seatRepository;

    @Test
    @DisplayName("정확히 동시에 100명이 동일 좌석 예약 시도 (Starting Gun 적용)")
    void concurrent_test() throws InterruptedException {
        // 1. 테스트 좌석 하나 생성
        Seat seat = seatRepository.save(new Seat(1L, "TEST=SEAT"));
        Long seatId = seat.getId();

        int numberOfThreads = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);

        // 추가: 모든 스레드가 동시에 출발하도록 대기시키는 래치 (출발선)
        CountDownLatch startLatch = new CountDownLatch(1);
        // 기존: 모든 스레드 작업이 끝날 때까지 메인 스레드를 대기시키는 래치 (결승선)
        CountDownLatch doneLatch = new CountDownLatch(numberOfThreads);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < numberOfThreads; i++) {
            final Long userId = (long) i;
            executorService.submit(() -> {
                try {
                    startLatch.await(); // 모든 스레드가 여기서 대기 (출발 신호 대기)
                    reservationService.holdSeat(new ReservationCreateCommand(seatId, userId));
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    doneLatch.countDown(); // 작업 완료 보고
                }
            });
        }

        // 3. 실행 (Starting Gun)
        startLatch.countDown(); // 대기 중인 100개의 스레드를 정확히 동시에 출발시킴

        // 4. 모든 작업 완료 대기
        doneLatch.await();

        // 5. 결과 출력 및 검증
        System.out.println("성공 횟수: " + successCount.get());
        System.out.println("실패 횟수: " + failCount.get());

        // 검증 1: 메모리상 성공 횟수는 1번이어야 한다.
        assertThat(successCount.get()).isEqualTo(1);

        // 검증 2: 실제 DB에도 1건만 저장되었는지 무결성 검증 (reservationRepository 필요)
        // List<Reservation> reservations = reservationRepository.findAll();
        // assertThat(reservations.size()).isEqualTo(1);
    }

}
