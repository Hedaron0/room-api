package com.dsu.roomapi;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api/rooms")
@RestController

public class RoomController {

    private final List<Room> rooms = new ArrayList<>(List.of(
            new Room(1L, "Seminar A", 8),
            new Room(2L, "Study Pod", 4),
            new Room(3L, "Rooftop Room", 12)
    ));

    private final AtomicLong nextId = new AtomicLong(4L);

    @GetMapping
    public List<Room> all(
        @RequestParam(required = false) Integer minCapacity,
        @RequestParam(defaultValue = "") String keyword){

        List<Room> result = new ArrayList<>();

        for (Room room : rooms){
            if(minCapacity != null && room.capacity() < minCapacity){
                continue;
            }
            if(!keyword.isEmpty() && !room.name().toLowerCase().contains(keyword.toLowerCase())){
                continue;
            }
            result.add(room);
        }

        return result;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Room> one(@PathVariable Long id) {
        Room found = null;
        for (Room room : rooms) {
            if (room.id().equals(id)) {
                found = room;
                break;
            }

        }
        if (found == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(found);
    }

    @PostMapping
    public ResponseEntity<Room> create(@RequestBody RoomCreateRequest request) {
        Room room = new Room(nextId.getAndIncrement(), request.name(), request.capacity());
        rooms.add(room);

        URI location = URI.create("/api/rooms/" + room.id());
        return ResponseEntity.created(location).body(room);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Room> update(@PathVariable Long id, @RequestBody RoomCreateRequest request) {
        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).id().equals(id)) {
                Room updated = new Room(id, request.name(), request.capacity());
                rooms.set(i, updated);
                return ResponseEntity.ok(updated);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        for (Room room : rooms) {
            if (room.id().equals(id)) {
                rooms.remove(room);
                return ResponseEntity.noContent().build();
            }
        }
        return ResponseEntity.notFound().build();
    }
}
