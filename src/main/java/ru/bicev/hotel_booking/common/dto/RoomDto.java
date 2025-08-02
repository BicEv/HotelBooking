package ru.bicev.hotel_booking.common.dto;

import java.util.UUID;


public record RoomDto(UUID id, String number, String type) {

}
