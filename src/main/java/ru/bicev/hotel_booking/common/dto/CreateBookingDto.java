package ru.bicev.hotel_booking.common.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;


public record CreateBookingDto(UUID userId, UUID roomId, LocalDate checkIn, LocalDate checkOut, BigDecimal amount) {

}
