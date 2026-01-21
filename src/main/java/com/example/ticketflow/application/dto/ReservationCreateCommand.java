package com.example.ticketflow.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationCreateCommand {
    private Long seatId;
    private Long userId;
}
