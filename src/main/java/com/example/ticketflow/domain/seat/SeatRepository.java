package com.example.ticketflow.domain.seat;

import org.springframework.data.jpa.repository.JpaRepository;// DAO와 비슷한 개념

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    // select * from seat where hall_id = ?
    List<Seat> findAllByHallId(Long hallId);
}
