package ru.bicev.hotel_booking.booking.repository;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ru.bicev.hotel_booking.common.entity.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {

    @Query("""
            SELECT COUNT(b) > 0 FROM Booking b
            WHERE b.room.id = :roomId
            AND b.checkIn < :checkOut
            AND :checkIn < b.checkOut
            """)
    boolean existsDatesOverlapping(
            @Param("roomId") UUID roomId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut);

    default boolean isAvalable(UUID roomId, LocalDate checkIn, LocalDate checkOut) {
        return !existsDatesOverlapping(roomId, checkIn, checkOut);
    }
}
