package ru.bicev.hotel_booking.common.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentRequestedEvent(UUID eventId, Instant timestamp, UUID bookingId, UUID userId, BigDecimal amount)
        implements Event {

}
