package ru.bicev.hotel_booking.room.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ru.bicev.hotel_booking.common.dto.CreateRoomDto;
import ru.bicev.hotel_booking.common.dto.RoomDto;
import ru.bicev.hotel_booking.common.entity.Room;
import ru.bicev.hotel_booking.common.exception.RoomNotFoundException;
import ru.bicev.hotel_booking.room.repository.RoomRepository;

@Service
public class RoomService {

    private static final Logger logger = LoggerFactory.getLogger(RoomService.class);
    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public RoomDto createRoom(CreateRoomDto roomDto) {
        var room = mapToEntity(roomDto);
        var savedRoom = roomRepository.save(room);
        logger.info("Created room with id: {}", savedRoom.getId());
        return mapToDto(savedRoom);

    }

    public RoomDto getRoomByUUID(UUID roomId) {
        logger.info("Getting room with id: {}", roomId);
        return roomRepository.findById(roomId).map(this::mapToDto)
                .orElseThrow(() -> new RoomNotFoundException("Room was not found for: " + roomId.toString()));
    }

    public RoomDto updateRoom(RoomDto roomDto) {
        var room = roomRepository.findById(roomDto.id())
                .orElseThrow(() -> new RoomNotFoundException("Room was not found for: " + roomDto.id().toString()));
        room.setNumber(roomDto.number());
        room.setType(roomDto.type());
        var updatedRoom = roomRepository.save(room);
        logger.info("Updated room with id: {}", updatedRoom.getId());
        return mapToDto(updatedRoom);
    }

    public void deleteRoom(UUID roomId) {
        logger.info("Deleting room with id: {}", roomId);
        roomRepository.deleteById(roomId);
    }

    public boolean roomExistsByUUID(UUID roomId) {
        logger.info("Checking room with id: {}", roomId);
        return roomRepository.existsById(roomId);
    }

    private RoomDto mapToDto(Room room) {
        return new RoomDto(room.getId(), room.getNumber(), room.getType());
    }

    private Room mapToEntity(CreateRoomDto roomDto) {
        return new Room(UUID.randomUUID(), roomDto.number(), roomDto.type());
    }

}
