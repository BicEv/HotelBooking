package ru.bicev.hotel_booking.room.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.bicev.hotel_booking.common.entity.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, UUID> {
    
}
