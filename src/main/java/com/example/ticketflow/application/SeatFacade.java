package com.example.ticketflow.application;

import com.example.ticketflow.application.dto.SeatStatusDto;
import com.example.ticketflow.domain.reservation.Reservation;
import com.example.ticketflow.domain.reservation.ReservationRepository;
import com.example.ticketflow.domain.reservation.ReservationStatus;
import com.example.ticketflow.domain.seat.Seat;
import com.example.ticketflow.domain.seat.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

// ServiceImpl과 같은 역할
@Service
@RequiredArgsConstructor
public class SeatFacade {

    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;

    @Transactional
    public List<SeatStatusDto> getAllSeats(Long hallId) {
        // 1.모든 좌석 조회
        List<Seat> seats = seatRepository.findAllByHallId(hallId);

        // 2. 좌석 id 목록 추출
        List<Long> seatIds = seats.stream().map(Seat::getId).toList();

        // 3. 해당 좌석들의 유요한 예약 조회
        List<Reservation> reservations = reservationRepository.findBySeatIdInAndStatusNot(seatIds, ReservationStatus.EXPIRED);

        // 4. 조회를 빠를게 하기 위해 Map으로 변환
        Map<Long, Reservation> reservationMap = reservations.stream()
                .collect(Collectors.toMap(Reservation::getSeatId, Function.identity()));

        // 5. 좌석 + 예약 정보 합치기
        return seats.stream().map(seat -> {
            Reservation reservation = reservationMap.get(seat.getId());

            String status = "AVAILABLE";
            Long resId = null;
            Long userId = null;

            if (reservation != null) {
                status = reservation.getStatus().name();
                resId = reservation.getId();
                userId = reservation.getUserId();
            }

            return new SeatStatusDto(seat.getId(), seat.getSeatCode(), status, resId, userId );
        }).toList();
    }

}
