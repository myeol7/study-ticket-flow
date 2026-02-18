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
    @DisplayName("동시에 100명 이 동일 좌석 예약 시도")
    void concurrent_test() throws InterruptedException {
        // 1. 테스트 좌석 하나 생성
        Seat seat = seatRepository.save(new Seat(1L, "TEST=SEAT"));
        Long seatId = seat.getId();

        // 2. 동시성 환경 세팅
        int numberOfThreads = 100;
        // 스레드 풀 생성
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        // 문지기
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        // 성공 횟수 카운터
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // 3. 실행
        for (int i = 0; i < numberOfThreads; i++) {
            final Long userId = (long) i; // 서로다른 유저
            executorService.submit(() -> {
                try {
                    // 예약 시도
                    reservationService.holdSeat(new ReservationCreateCommand(seatId, userId));
                    // 성공 카운트 증가
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    // 실패 카운트 증가
                    failCount.incrementAndGet();
                } finally {
                    // 문지기 감소
                    latch.countDown();
                }
            });
        }

        // 4. 모든 작업 완료 대기
        latch.await();

        // 5. 결과 출력
        System.out.println("시도 횟수: " + numberOfThreads);
        System.out.println("성공 횟수: " + successCount.get());
        System.out.println("실패 횟수: " + failCount.get());

        // 우리의 기대: 성공은 딱 1번이어야 한다.
        // 하지만 락이 없다면? 아마 깨질 것이다.
        assertThat(successCount.get()).isEqualTo(1);
    }

}
