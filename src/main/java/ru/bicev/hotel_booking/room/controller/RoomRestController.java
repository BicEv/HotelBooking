package ru.bicev.hotel_booking.room.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.bicev.hotel_booking.common.dto.CreateRoomDto;
import ru.bicev.hotel_booking.common.dto.RoomDto;
import ru.bicev.hotel_booking.common.exception.RoomNotFoundException;
import ru.bicev.hotel_booking.room.service.RoomService;

@RestController
@RequestMapping("/api/rooms")
public class RoomRestController {

    private final RoomService roomService;

    public RoomRestController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping
    public ResponseEntity<RoomDto> createRoom(@RequestBody CreateRoomDto roomDto) {
        var createdRoom = roomService.createRoom(roomDto);
        return ResponseEntity.ok(createdRoom);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomDto> getRoomByUUID(@PathVariable UUID roomId) {
        var foundRoom = roomService.getRoomByUUID(roomId);
        return ResponseEntity.ok(foundRoom);
    }

    @PutMapping
    public ResponseEntity<RoomDto> updateRoom(@RequestBody RoomDto roomDto) {
        var updatedRoom = roomService.updateRoom(roomDto);
        return ResponseEntity.ok(updatedRoom);
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable UUID roomId) {
        roomService.deleteRoom(roomId);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(RoomNotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(RoomNotFoundException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException() {
        return ResponseEntity.internalServerError().build();
    }

}
