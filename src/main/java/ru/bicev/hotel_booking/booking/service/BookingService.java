package ru.bicev.hotel_booking.booking.service;

import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import ru.bicev.hotel_booking.booking.kafka.BookingEventProducer;
import ru.bicev.hotel_booking.booking.repository.BookingRepository;
import ru.bicev.hotel_booking.common.dto.BookingDto;
import ru.bicev.hotel_booking.common.dto.CreateBookingDto;
import ru.bicev.hotel_booking.common.dto.RoomDto;
import ru.bicev.hotel_booking.common.entity.Booking;
import ru.bicev.hotel_booking.common.enums.BookingStatus;
import ru.bicev.hotel_booking.common.exception.BookingNotFoundException;
import ru.bicev.hotel_booking.common.exception.BookingOverlappingException;
import ru.bicev.hotel_booking.common.exception.InvalidBooingDateException;
import ru.bicev.hotel_booking.common.util.RoomMapper;
import ru.bicev.hotel_booking.room.service.RoomService;

@Service
public class BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);
    private final BookingRepository bookingRepository;
    private final RoomService roomService;
    private final BookingEventProducer bookingEventProducer;
    private final ConcurrentHashMap<UUID, ReentrantLock> roomLocks = new ConcurrentHashMap<>();

    public BookingService(BookingRepository bookingRepository, RoomService roomService,
            BookingEventProducer bookingEventProducer) {
        this.bookingRepository = bookingRepository;
        this.roomService = roomService;
        this.bookingEventProducer = bookingEventProducer;
    }

    private ReentrantLock getLock(UUID roomId) {
        roomLocks.putIfAbsent(roomId, new ReentrantLock());
        logger.info("Creating a lock for a room: {}", roomId);
        return roomLocks.get(roomId);
    }

    @Transactional
    public BookingDto createBooking(CreateBookingDto createBookingDto) {
        validateBookingDates(createBookingDto.checkIn(), createBookingDto.checkOut());
        var roomDto = roomService.getRoomByUUID(createBookingDto.roomId());

        var lock = getLock(roomDto.id());

        lock.lock();
        try {
            if (bookingRepository.existsDatesOverlapping(createBookingDto.roomId(), createBookingDto.checkIn(),
                    createBookingDto.checkOut())) {
                logger.warn("BookingOverlappingException in BookingService id: {}, checkIn: {}, checkOut: {}",
                        roomDto.id(), createBookingDto.checkIn(), createBookingDto.checkOut());
                throw new BookingOverlappingException("Booking is not available for this dates");
            }

            var booking = createBooking(createBookingDto, roomDto);
            var saved = bookingRepository.save(booking);

            bookingEventProducer.sendBookingCreatedEvent(saved);
            logger.info("Booking created: {}", saved.getId());
            return mapToDto(saved);
        } finally {
            lock.unlock();
        }

    }

    public BookingDto getBookingByUUID(UUID bookingId) {
        logger.info("Getting booking with id: {}", bookingId);
        return bookingRepository.findById(bookingId).map(this::mapToDto)
                .orElseThrow(() -> new BookingNotFoundException("Booking is not found: " + bookingId));
    }

    @Transactional
    public void deleteBooking(UUID bookingId) {
        bookingRepository.deleteById(bookingId);
        logger.info("Deleting booking: {}", bookingId);
    }

    @Transactional
    public void confirmBookingStatus(UUID bookingId) {
        var booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking is not found: " + bookingId));
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);
        logger.info("Booking confirmed: {}", bookingId);        

    }

    @Transactional
    public void cancelBooking(UUID bookingId) {
        var booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found"));

        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Cannot cancel a confirmed booking");
        }

        bookingRepository.delete(booking);
        logger.info("Booking cancelled: {}", bookingId);
    }

    private BookingDto mapToDto(Booking booking) {
        var bookingDto = new BookingDto(booking.getId(),
                booking.getUserId(),
                booking.getRoom().getId(),
                booking.getCheckIn(),
                booking.getCheckOut(),
                booking.getAmount(),
                booking.getStatus());
        return bookingDto;
    }

    private Booking createBooking(CreateBookingDto bookingDto, RoomDto roomDto) {
        var booking = new Booking(UUID.randomUUID(), bookingDto.userId(), null, bookingDto.checkIn(),
                bookingDto.checkOut(), bookingDto.amount(), BookingStatus.NEW);
        booking.setRoom(
                RoomMapper.toEntity(roomDto));
        return booking;
    }

    private void validateBookingDates(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn.isAfter(checkOut) || checkIn.isBefore(LocalDate.now())) {
            throw new InvalidBooingDateException("Invalid booking dates");
        }
    }

}
