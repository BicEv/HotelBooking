package ru.bicev.hotel_booking.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.bicev.hotel_booking.booking.kafka.BookingEventProducer;
import ru.bicev.hotel_booking.booking.repository.BookingRepository;
import ru.bicev.hotel_booking.booking.service.BookingService;
import ru.bicev.hotel_booking.common.dto.BookingDto;
import ru.bicev.hotel_booking.common.dto.CreateBookingDto;
import ru.bicev.hotel_booking.common.dto.RoomDto;
import ru.bicev.hotel_booking.common.entity.Booking;
import ru.bicev.hotel_booking.common.entity.Room;
import ru.bicev.hotel_booking.common.enums.BookingStatus;
import ru.bicev.hotel_booking.common.exception.BookingNotFoundException;
import ru.bicev.hotel_booking.common.exception.BookingOverlappingException;
import ru.bicev.hotel_booking.common.exception.InvalidBooingDateException;
import ru.bicev.hotel_booking.common.exception.RoomNotFoundException;
import ru.bicev.hotel_booking.room.service.RoomService;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RoomService roomService;

    @Mock
    private BookingEventProducer bookingEventProducer;

    @InjectMocks
    private BookingService bookingService;

    UUID roomId = UUID.randomUUID();
    private RoomDto testRoom = new RoomDto(roomId, "1408", "Regular");

    UUID bookingId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    BigDecimal amount = new BigDecimal(4);
    Room room = new Room(roomId, "1408", "Regular");
    LocalDate checkIn = LocalDate.of(2026, 1, 1);
    LocalDate checkOut = LocalDate.of(2026, 1, 5);

    private BookingDto bookingDto = new BookingDto(bookingId, userId, roomId, checkIn,
            checkOut, amount, BookingStatus.NEW);

    private CreateBookingDto createBookingDto = new CreateBookingDto(userId, roomId,
            checkIn, checkOut, amount);

    private Booking booking = new Booking(bookingId, userId, room, checkIn, checkOut, amount, BookingStatus.NEW);
    private Booking confirmedBooking = new Booking(bookingId, userId, room, checkIn, checkOut, amount,
            BookingStatus.CONFIRMED);

    @Test
    public void createBookingSuccess() {
        when(roomService.getRoomByUUID(any())).thenReturn(testRoom);
        when(bookingRepository.existsDatesOverlapping(roomId, checkIn, checkOut)).thenReturn(false);
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDto result = bookingService.createBooking(createBookingDto);

        assertEquals(bookingDto.id(), result.id());
        assertEquals(bookingDto.checkIn(), result.checkIn());
        assertEquals(bookingDto.checkOut(), result.checkOut());

        verify(bookingEventProducer, times(1)).sendBookingCreatedEvent(any());

    }

    @Test
    public void createBookingOverlappingException() {
        when(roomService.getRoomByUUID(any())).thenReturn(testRoom);
        when(bookingRepository.existsDatesOverlapping(roomId, checkIn, checkOut)).thenReturn(true);

        assertThrows(BookingOverlappingException.class, () -> bookingService.createBooking(createBookingDto));
    }

    @Test
    public void createBookingRoomNotFoundException() {
        when(roomService.getRoomByUUID(roomId)).thenThrow(RoomNotFoundException.class);

        assertThrows(RoomNotFoundException.class, () -> bookingService.createBooking(createBookingDto));
    }

    @Test
    public void createBookingInvalidBookingDateException() {

        assertThrows(InvalidBooingDateException.class, () -> bookingService.createBooking(
                new CreateBookingDto(userId, roomId, LocalDate.of(2025, 11, 5), LocalDate.of(2025, 10, 1), amount)));
    }

    @Test
    public void getBookingByUUIDSuccess() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.getBookingByUUID(bookingId);

        assertEquals(booking.getId(), result.id());
        assertEquals(booking.getCheckIn(), result.checkIn());
        assertEquals(booking.getCheckOut(), result.checkOut());
    }

    @Test
    public void getBookingByUUIDBookingNotFoundException() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class, () -> bookingService.getBookingByUUID(bookingId));

    }

    @Test
    public void deleteBookingSuccess() {
        bookingService.deleteBooking(bookingId);

        verify(bookingRepository).deleteById(bookingId);
    }

    @Test
    public void confirmBookingStatusSuccess() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        bookingService.confirmBookingStatus(bookingId);

        assertEquals(BookingStatus.CONFIRMED, booking.getStatus());

        verify(bookingRepository).save(booking);
    }

    @Test
    public void confirmBookingStatusBookingNotFoundException() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class, () -> bookingService.confirmBookingStatus(bookingId));

    }

    @Test
    public void cancelBookingSuccess() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        bookingService.cancelBooking(bookingId);

        verify(bookingRepository).delete(booking);
    }

    @Test
    public void cancelBookingBookingNotFoundException() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class, () -> bookingService.cancelBooking(bookingId));

    }

    @Test
    public void cancelBookingIllegalStateException() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(confirmedBooking));

        assertThrows(IllegalStateException.class, () -> bookingService.cancelBooking(bookingId));

    }

}
