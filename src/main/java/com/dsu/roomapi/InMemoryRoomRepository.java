package com.dsu.roomapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

@Repository public class InMemoryRoomRepository implements RoomRepository {

    private final List<Room> rooms = new ArrayList<>(List.of(
            new Room(1L, "Seminar A", 8),
            new Room(2L, "Study Pod", 4),
            new Room(3L, "Rooftop Room", 12)

    ));

    private final AtomicLong nextId = new AtomicLong(4L);

    public List<Room> findAll(){
        return rooms;
    }

    public Optional<Room> findById(Long id){
        return rooms.stream().filter(r -> r.id().equals(id)).findFirst();
    }

    public Room save(Room room) {
        if (room.id() == null) {
            Room created = new Room(nextId.getAndIncrement(), room.name(), room.capacity());
            rooms.add(created);
            return created;
        }
        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).id().equals(room.id())) {
                rooms.set(i, room);
                return room;
            }
        }
        throw new IllegalArgumentException("No room with id " + room.id());
    }
    public boolean deleteById(Long id){
        return rooms.removeIf(r -> r.id().equals(id));
        }
    }
