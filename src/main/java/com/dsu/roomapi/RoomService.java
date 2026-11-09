package com.dsu.roomapi;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;

    public RoomService(RoomRepository roomRepository, ReservationRepository reservationRepository) {
        this.roomRepository = roomRepository;
        this.reservationRepository = reservationRepository;
    }

    public List<Room> search(Integer minCapacity, String keyword) {
        if (minCapacity == null) {
            return roomRepository.findByNameContainingIgnoreCase(keyword);
        }
        return roomRepository.findByCapacityGreaterThanEqualAndNameContainingIgnoreCase(minCapacity, keyword);
    }

    public long count() {
        return roomRepository.count();
    }

    public Room get(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("room " + id + " not found"));
    }

    public Room create(String name, int capacity) {
        return roomRepository.save(new Room(null, name, capacity));
    }

    @Transactional
    public Room update(Long id, String name, int capacity) {
        Room room = get(id);
        room.update(name, capacity);
        return room;
    }

    @Transactional
    public void delete(Long id) {
        Room room = get(id);
        if (reservationRepository.existsByRoomId(id)) {
            throw new ConflictException("room " + id + " has reservations");
        }
        roomRepository.delete(room);
    }
}
