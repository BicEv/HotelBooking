package ru.bicev.hotel_booking.common.util;

import java.time.Instant;
import java.util.UUID;

import ru.bicev.hotel_booking.common.entity.Booking;
import ru.bicev.hotel_booking.common.event.BookingCreatedEvent;

public class EventMapper {

    public static BookingCreatedEvent mapBookingToEvent(Booking booking, UUID eventId, Instant timestamp) {
        BookingCreatedEvent event = new BookingCreatedEvent(eventId,
                timestamp,
                booking.getId(),
                booking.getUserId(),
                booking.getRoom().getId(),
                booking.getCheckIn(),
                booking.getCheckOut(),
                booking.getAmount());
        return event;
    }

}
