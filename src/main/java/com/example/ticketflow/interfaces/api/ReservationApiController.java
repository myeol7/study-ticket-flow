package com.example.ticketflow.interfaces.api;

import com.example.ticketflow.application.ReservationService;
import com.example.ticketflow.application.dto.ReservationCreateCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/api/reservations/{id}/confirm")
    public ResponseEntity<String> confirmReservation(@PathVariable Long id, @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {

        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            return ResponseEntity.badRequest().body("Idempotency-Key 헤더가 필요합니다.");
        }

        try{
            reservationService.confirmReservation(id, idempotencyKey);
            return ResponseEntity.ok("예약 확정 성공.");
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).body("중복된 요청입니다.");
        }catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/api/reservations/{id}/cancel")
    public ResponseEntity<String> cancelReservation(@PathVariable Long id) {
        try {
            reservationService.cancelReservation(id);
            return ResponseEntity.ok("예약 취소 성공.");
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
