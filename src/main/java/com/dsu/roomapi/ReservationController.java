package com.dsu.roomapi;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RequestMapping("api")
@RestController
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/rooms/{roomId}/reservations")
    public List<ReservationResponse> byRoom(@PathVariable Long roomId) {
        return reservationService.findByRoom(roomId).stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @PostMapping("/rooms/{roomId}/reservations")
    public ResponseEntity<ReservationResponse> create(@PathVariable Long roomId,
                                                      @Valid @RequestBody ReservationCreateRequest request) {
        Reservation r = reservationService.create(roomId, request.reservedBy(), request.startTime(), request.endTime());
        return ResponseEntity.created(URI.create("/api/reservations/" + r.getId())).body(ReservationResponse.from(r));
    }

    @GetMapping("/reservations")
    public List<ReservationResponse> byPerson(@RequestParam String reservedBy) {
        return reservationService.findByReservedBy(reservedBy).stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @GetMapping("/reservations/{id}")
    public ReservationResponse findOne(@PathVariable Long id) {
        return ReservationResponse.from(reservationService.get(id));
    }

    @DeleteMapping("/reservations/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        reservationService.delete(id);
    }
}
