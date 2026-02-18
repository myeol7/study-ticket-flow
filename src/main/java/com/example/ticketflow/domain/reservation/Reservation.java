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
    public Reservation(Long seatId, Long userId, LocalDateTime expiredAt) {
        this.seatId = seatId;
        this.userId = userId;
        this.status = ReservationStatus.HELD;
        this.expiredAt = expiredAt;
    }

    public void confirm() {
        if (this.status != ReservationStatus.HELD) {
            throw new IllegalStateException("임시 예약 상태인 경우에만 확정할 수 있습니다.");
        }

        // TODO: 여기서 만료 시간(expiredAt) 체크 로직도 들어가야 함 (나중에 추가)

        this.status = ReservationStatus.CONFIRMED;
    }

    public void expire() {
        if (this.status == ReservationStatus.HELD) {
            this.status = ReservationStatus.EXPIRED;
        }
    }
}
