package com.example.ticketflow.interfaces.api;

import com.example.ticketflow.application.ReservationService;
import com.example.ticketflow.application.dto.ReservationCreateCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReservationApiController {
    private final ReservationService reservationService;

    @PostMapping("/api/reservations")
    public ResponseEntity<String> createReservation(@RequestBody ReservationCreateCommand command) {
       try {
           reservationService.holdSeat(command);
           return ResponseEntity.ok("예약 성공.");
       } catch (IllegalStateException e) {
           return ResponseEntity.badRequest().body(e.getMessage());
       }
}

}
