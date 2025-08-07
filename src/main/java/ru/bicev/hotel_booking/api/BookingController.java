package ru.bicev.hotel_booking.api;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.bicev.hotel_booking.booking.service.BookingService;
import ru.bicev.hotel_booking.common.dto.BookingDto;
import ru.bicev.hotel_booking.common.dto.CreateBookingDto;
import ru.bicev.hotel_booking.common.exception.BookingNotFoundException;
import ru.bicev.hotel_booking.common.exception.BookingOverlappingException;
import ru.bicev.hotel_booking.common.exception.InvalidBooingDateException;

@RestController
@RequestMapping("/api/booking")
public class BookingController {

    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingDto> create(@RequestBody CreateBookingDto createBooking) {

        var booking = bookingService.createBooking(createBooking);
        logger.info("Booking created in controller: {}", booking.id());
        return ResponseEntity.ok(booking);

    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getById(@PathVariable UUID bookingId) {
        var booking = bookingService.getBookingByUUID(bookingId);
        logger.info("Get Booking in controller: {}", bookingId);
        return ResponseEntity.ok(booking);
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID bookingId) {
        bookingService.deleteBooking(bookingId);
        logger.info("Booking deleted in controller: {}", bookingId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{bookingId}/cancel")
    public ResponseEntity<String> cancelById(@PathVariable UUID bookingId) {
        bookingService.cancelBooking(bookingId);
        logger.info("Booking cancelled in controller: {}", bookingId);
        return ResponseEntity.ok("Booking cancelled");
    }

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<String> handleNotFoundException() {
        logger.warn("BookingNotFoundException handled in controller");
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(BookingOverlappingException.class)
    public ResponseEntity<String> handleOverlappingException() {
        logger.warn("BookingOverlappingException handled in controller");
        return ResponseEntity.status(409).body("Booking overlaps with existing one");
    }

    @ExceptionHandler(InvalidBooingDateException.class)
    public ResponseEntity<String> handleInvalidBookingDateException() {
        logger.warn("InvalidBookingDateException handled in controller");
        return ResponseEntity.status(400).body("Invalid booking dates");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        logger.error("RuntimeExcption in controller: {}", ex);
        return ResponseEntity.status(500).body(ex.getMessage());
    }

}
