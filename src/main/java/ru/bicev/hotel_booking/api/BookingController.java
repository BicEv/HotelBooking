package ru.bicev.hotel_booking.api;

import java.util.UUID;

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

@RestController
@RequestMapping("/api/booking")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingDto> create(@RequestBody CreateBookingDto createBooking) {

        var booking = bookingService.createBooking(createBooking);

        return ResponseEntity.ok(booking);

    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getById(@PathVariable UUID bookingId) {
        var booking = bookingService.getBookingByUUID(bookingId);

        return ResponseEntity.ok(booking);
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID bookingId) {
        bookingService.deleteBooking(bookingId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{bookingId}/cancel")
    public ResponseEntity<String> cancelById(@PathVariable UUID bookingId) {
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.ok("Booking cancelled");
    }

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<String> handleNotFoundException() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(BookingOverlappingException.class)
    public ResponseEntity<String> handleOverlappingException() {
        return ResponseEntity.status(409).body("Booking overlaps with existing one");
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(500).body(ex.getMessage());
    }

}
