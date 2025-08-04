package ru.bicev.hotel_booking.room.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import ru.bicev.hotel_booking.common.dto.CreateRoomDto;
import ru.bicev.hotel_booking.common.dto.RoomDto;
import ru.bicev.hotel_booking.common.entity.Room;
import ru.bicev.hotel_booking.common.exception.RoomNotFoundException;
import ru.bicev.hotel_booking.room.repository.RoomRepository;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public RoomDto createRoom(CreateRoomDto roomDto) {
        var room = mapToEntity(roomDto);
        var savedRoom = roomRepository.save(room);
        return mapToDto(savedRoom);

    }

    public RoomDto getRoomByUUID(UUID roomId) {
        return roomRepository.findById(roomId).map(this::mapToDto)
                .orElseThrow(() -> new RoomNotFoundException("Room was not found for: " + roomId.toString()));
    }

    public RoomDto updateRoom(RoomDto roomDto) {
        var room = roomRepository.findById(roomDto.id())
                .orElseThrow(() -> new RoomNotFoundException("Room was not found for: " + roomDto.id().toString()));
        room.setNumber(roomDto.number());
        room.setType(roomDto.type());
        var updatedRoom = roomRepository.save(room);
        return mapToDto(updatedRoom);
    }

    public void deleteRoom(UUID roomId) {
        roomRepository.deleteById(roomId);
    }

    private RoomDto mapToDto(Room room) {
        return new RoomDto(room.getId(), room.getNumber(), room.getType());
    }

    private Room mapToEntity(RoomDto roomDto) {
        UUID id = roomDto.id() != null ? roomDto.id() : UUID.randomUUID();
        return new Room(id, roomDto.number(), roomDto.type());
    }

    private Room mapToEntity(CreateRoomDto roomDto) {
        return new Room(UUID.randomUUID(), roomDto.number(), roomDto.type());
    }

}
