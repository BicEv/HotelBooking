package ru.bicev.hotel_booking.common.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import ru.bicev.hotel_booking.common.enums.BookingStatus;

public record BookingDto(UUID id, UUID userId, UUID roomId, LocalDate checkIn, LocalDate checkOut, BigDecimal amount,
        BookingStatus status) {

}
