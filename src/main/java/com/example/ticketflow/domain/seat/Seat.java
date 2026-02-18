package com.example.ticketflow.domain.seat;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

// VO와 비슷한 개념
@Entity
@Table(name = "seat")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long hallId;

    @Column(nullable = false)
    private String seatCode;

    public Seat(Long hallId, String seatCode) {
        this.hallId = hallId;
        this.seatCode = seatCode;
    }
}
