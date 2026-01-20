package com.example.ticketflow.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

// 화면에서 보여줄 데이터만 담은 객체
@Getter
@AllArgsConstructor
public class SeatStatusDto {
    private Long SeatId;
    private String seatCode;
    private String status;      // AVAILABLE, HELD, CONFIRMED(화면과 enum의 차이 있음)
    private Long reservationId; // 예약 없으면 null
    private Long userId;        // 예약 없으면 null
}
