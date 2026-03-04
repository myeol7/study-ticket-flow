package com.example.ticketflow.domain.reservation;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Reservation r WHERE r.id = :id")
    Optional<Reservation> findByIdWithLock(@Param("id") Long id);

    // select * from reservations where seat_id in (...) and status != ?
    List<Reservation> findBySeatIdInAndStatusNot(List<Long> seatIds, ReservationStatus status);

    // select * from reservations where seat_id in (...) and status in (...)
    List<Reservation> findBySeatIdInAndStatusIn(List<Long> seatIds, List<ReservationStatus> statuses);

    // select * from reservations where seat_id = ? and status != ?
    Optional<Reservation> findBySeatIdAndStatusNot(Long seatId, ReservationStatus status);

    // select * from reservations where seat_id = ? and status in (...)
    Optional<Reservation> findBySeatIdAndStatusIn(Long seatId, List<ReservationStatus> statuses);

    // select * from reservations where status = ? and expired_at < ?
    List<Reservation> findByStatusAndExpiredAtLessThan(ReservationStatus status, LocalDateTime now);
}
