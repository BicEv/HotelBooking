package ru.bicev.hotel_booking.common.exception;

public class BookingOverlappingException extends RuntimeException {
    public BookingOverlappingException(String message) {
        super(message);
    }

}
