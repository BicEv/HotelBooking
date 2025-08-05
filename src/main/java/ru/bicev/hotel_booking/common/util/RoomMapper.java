package ru.bicev.hotel_booking.common.util;

import ru.bicev.hotel_booking.common.dto.RoomDto;
import ru.bicev.hotel_booking.common.entity.Room;

public class RoomMapper {

    public static Room toEntity(RoomDto roomDto) {
        var room = new Room(roomDto.id(), roomDto.number(), roomDto.type());
        return room;
    }

    public static RoomDto toDto(Room room) {
        var roomDto = new RoomDto(room.getId(), room.getNumber(), room.getType());
        return roomDto;
    }

}
