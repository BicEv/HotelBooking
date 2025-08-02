package ru.bicev.hotel_booking.common.event;

import java.time.Instant;
import java.util.UUID;

public record PaymentCompletedEvent(UUID eventId, Instant timestamp, UUID bookingId, UUID paymentId, UUID userId)
                implements Event {

}
