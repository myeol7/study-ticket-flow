package com.example.ticketflow.application;

import com.example.ticketflow.application.dto.ReservationCreateCommand;
import com.example.ticketflow.domain.reservation.Reservation;
import com.example.ticketflow.domain.reservation.ReservationRepository;
import com.example.ticketflow.domain.reservation.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    @Transactional
    public Long holdSeat(ReservationCreateCommand command) {

        // 1. 좌석이 이미 예약되었는지 확인
        reservationRepository.findBySeatIdAndStatusNot(command.getSeatId(), ReservationStatus.EXPIRED)
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
}
