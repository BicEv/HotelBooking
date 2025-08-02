package ru.bicev.hotel_booking.common.event;

import java.time.Instant;
import java.util.UUID;

public interface Event {
    UUID eventId();

    Instant timestamp();

    UUID bookingId();

}
