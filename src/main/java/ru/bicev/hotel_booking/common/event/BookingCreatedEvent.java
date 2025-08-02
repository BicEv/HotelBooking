package ru.bicev.hotel_booking.common.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record BookingCreatedEvent(UUID eventId, Instant timestamp, UUID bookingId, UUID userId, UUID roomId,
                LocalDate checkIn, LocalDate checkOut, BigDecimal amount)
                implements Event {

}
