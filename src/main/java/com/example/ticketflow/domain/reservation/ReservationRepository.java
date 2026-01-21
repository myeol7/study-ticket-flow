package com.example.ticketflow.domain.reservation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // select * from reservations where seat_id in (...) and status != ?
    List<Reservation> findBySeatIdInAndStatusNot(List<Long> seatIds, ReservationStatus status);

    // select * from reservations where seat_id = ? and status != ?
    Optional<Reservation> findBySeatIdAndStatusNot(Long seatId, ReservationStatus status);
}
