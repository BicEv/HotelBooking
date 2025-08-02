package ru.bicev.hotel_booking.common.event;

import java.time.Instant;
import java.util.UUID;

public record RoomReservedEvent(UUID eventId, Instant timestamp, UUID bookingId, UUID roomId) implements Event {

}
