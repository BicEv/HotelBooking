package ru.bicev.hotel_booking.common.event;

import java.time.Instant;
import java.util.UUID;

public record PaymentFailedEvent(UUID eventId, Instant timestamp, UUID bookingId, String errorMessage)
        implements Event {

}
