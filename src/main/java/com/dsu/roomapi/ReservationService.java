package com.dsu.roomapi;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomService roomService;

    public ReservationService(ReservationRepository reservationRepository, RoomService roomService) {
        this.reservationRepository = reservationRepository;
        this.roomService = roomService;
    }

    public List<Reservation> findByRoom(Long roomId) {
        roomService.get(roomId);
        return reservationRepository.findByRoomIdOrderByStartTime(roomId);
    }

    public List<Reservation> findByReservedBy(String reservedBy) {
        return reservationRepository.findByReservedByWithRoom(reservedBy);
    }

    public Reservation get(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("reservation " + id + " not found"));
    }

    @Transactional
    public Reservation create(Long roomId, String reservedBy, LocalDateTime start, LocalDateTime end) {
        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("startTime must be before endTime");
        }
        Room room = roomService.get(roomId);
        if (reservationRepository.existsByRoomIdAndStartTimeLessThanAndEndTimeGreaterThan(roomId, end, start)) {
            throw new ConflictException("room " + roomId + " is already booked at that time");
        }
        return reservationRepository.save(new Reservation(room, reservedBy, start, end));
    }

    @Transactional
    public void delete(Long id) {
        reservationRepository.delete(get(id));
    }
}
