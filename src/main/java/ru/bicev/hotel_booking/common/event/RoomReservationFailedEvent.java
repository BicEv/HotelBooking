package ru.bicev.hotel_booking.common.event;

import java.time.Instant;
import java.util.UUID;

public record RoomReservationFailedEvent(UUID eventId, Instant timestamp, UUID bookingId, UUID roomId, String reason)
                implements Event {

}
