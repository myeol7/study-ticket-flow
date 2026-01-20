package com.example.ticketflow.domain.reservation;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservation")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long seatId;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    private LocalDateTime expiredAt;

    // 예약 생성자
    public Reservation(Long seatId, Long userId, ReservationStatus status, LocalDateTime expiredAt) {
        this.seatId = seatId;
        this.userId = userId;
        this.status = ReservationStatus.HELD;
        this.expiredAt = expiredAt;
    }
}
