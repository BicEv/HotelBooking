package ru.bicev.hotel_booking.common.event;

import java.time.Instant;
import java.util.UUID;

public record BookingConfirmedEvent(UUID eventId, Instant timestamp, UUID bookingId) implements Event{

}
