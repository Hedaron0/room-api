package com.dsu.roomapi;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RequestMapping("api/rooms")
@RestController
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public List<RoomResponse> list(
            @RequestParam(required = false) Integer minCapacity,
            @RequestParam(defaultValue = "") String keyword) {
        return roomService.search(minCapacity, keyword).stream()
                .map(RoomResponse::from)
                .toList();
    }

    @Operation(summary = "Get one room by id", description = "200 with the room, 404 if it does not exist")
    @GetMapping("/{id}")
    public RoomResponse findOne(@PathVariable Long id) {
        return RoomResponse.from(roomService.get(id));
    }

    @Operation(summary = "Create a room", description = "Capacity must be between 1 and 20, otherwise 400")
    @PostMapping
    public ResponseEntity<RoomResponse> create(@Valid @RequestBody RoomCreateRequest request) {
        Room room = roomService.create(request.name(), request.capacity());
        return ResponseEntity.created(URI.create("/api/rooms/" + room.getId())).body(RoomResponse.from(room));
    }

    @PutMapping("/{id}")
    public RoomResponse replace(@PathVariable Long id, @Valid @RequestBody RoomCreateRequest request) {
        return RoomResponse.from(roomService.update(id, request.name(), request.capacity()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        roomService.delete(id);
    }
}
