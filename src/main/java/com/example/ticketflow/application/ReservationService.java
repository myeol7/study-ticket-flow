package com.example.ticketflow.application;

import com.example.ticketflow.application.dto.ReservationCreateCommand;
import com.example.ticketflow.domain.Idempotency.IdempotencyHistory;
import com.example.ticketflow.domain.Idempotency.IdempotencyHistoryRepository;
import com.example.ticketflow.domain.reservation.Reservation;
import com.example.ticketflow.domain.reservation.ReservationRepository;
import com.example.ticketflow.domain.reservation.ReservationStatus;
import com.example.ticketflow.domain.seat.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository; // 비관락을 위해 추가
    private final IdempotencyHistoryRepository idempotencyHistoryRepository; // 멱등성 처리를 위한 레포지토리

    @Transactional
    public Long holdSeat(ReservationCreateCommand command) {

        // 0. 좌석에 대한 비관적 락을 적용하여 동시성 문제 방지
        seatRepository.findByIdWithLock(command.getSeatId());

        // 1. 좌석이 이미 예약되었는지 확인
        reservationRepository.findBySeatIdAndStatusIn(command.getSeatId(), List.of(ReservationStatus.HELD, ReservationStatus.CONFIRMED))
                .ifPresent(reservation -> {
                    throw new IllegalStateException("이미 예약된 좌석입니다.");
                });

        Reservation reservation = new Reservation(
                command.getSeatId(),
                command.getUserId(),
                LocalDateTime.now().plusMinutes(5)
        );

        Reservation saved = reservationRepository.save(reservation);

        return saved.getId();
    }

    @Transactional
    public Long confirmReservation(Long reservationId, String idempotencyKey) {

        // 0. [Fast-Fail] 가장 먼저 멱등성 키를 저장 (중복이면 여기서 예외 터짐)
        idempotencyHistoryRepository.save(new IdempotencyHistory(idempotencyKey));

        // 1. 예약 조회
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약 ID입니다."));

        // 2. 예약 확정
        reservation.confirm();

        // 3. 예약 ID 반환, 저장은 트랜잭션 커밋 시점에 자동으로 처리됨
        return reservation.getId();
    }

    @Transactional
    public Long cancelReservation(Long reservationId) {
        // 1. 예약 조회
        Reservation reservation = reservationRepository.findByIdWithLock(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약 ID입니다."));

        // 2. 예약 취소 (엔티티에 위임)
        reservation.cancel();

        return reservation.getId();
    }

    @Transactional
    public int expireOverdueReservations() {
        List<Reservation> expiredReservations = reservationRepository.findByStatusAndExpiredAtLessThan(
                ReservationStatus.HELD,
                LocalDateTime.now()
        );

        for (Reservation reservation : expiredReservations) {
            reservation.expire();
        }

        return expiredReservations.size();
    }
}
