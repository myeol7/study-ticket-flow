package com.example.ticketflow.domain.seat;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;// DAO와 비슷한 개념
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    // select * from seat where hall_id = ?
    List<Seat> findAllByHallId(Long hallId);

    // 비관적 락을 건 조회 메서드
    // select * from seat where id = ? for update
//    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    @Query("SELECT s FROM Seat s WHERE s.id = :id")
//    Optional<Seat> findByIdWithLock(@Param("id") Long id);

    // 비관적 락(PESSIMISTIC)이 아니라 낙관적 락 강제 증가(OPTIMISTIC_FORCE_INCREMENT)
    // "값이 안 바뀌어도 버전을 강제로 1 올려라"
    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    @Query("select s from Seat s where s.id = :id")
    Optional<Seat> findByIdWithOptimisticLock(@Param("id") Long id);
}
