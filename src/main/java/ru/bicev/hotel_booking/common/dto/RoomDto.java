package ru.bicev.hotel_booking.common.dto;

import java.util.UUID;

import ru.bicev.hotel_booking.common.enums.RoomStatus;

public record RoomDto(UUID id, String number, String type, RoomStatus status) {

}
