package com.dsu.roomapi;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;

@RequestMapping("api/rooms")
@RestController
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService){
        this.roomService = roomService;
    }

    @GetMapping
    public List<Room> all(
        @RequestParam(required = false) Integer minCapacity,
        @RequestParam(defaultValue = "") String keyword){

        return roomService.search(minCapacity, keyword);
    }

    @Operation(summary = "Get one room by id", description = "Returns 200 with the room if it exists, 404 otherwise")
    @GetMapping("/{id}")
    public ResponseEntity<Room> one(@PathVariable Long id) {
        Optional<Room> found = roomService.findById(id);
        return found.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a room", description = "Capacity must be between 1 and 20, otherwise returns 400")
    @PostMapping
    public ResponseEntity<Room> create(@RequestBody RoomCreateRequest request) {
        try {
            Room room = roomService.create(request.name(), request.capacity());
            URI location = URI.create("/api/rooms/" + room.getId());
            return ResponseEntity.created(location).body(room);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Room> update(@PathVariable Long id, @RequestBody RoomCreateRequest request) {
        try {
            Optional<Room> updated = roomService.replace(id, request.name(), request.capacity());
            return updated.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            boolean removed = roomService.delete(id);
            if(!removed) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.noContent().build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}
