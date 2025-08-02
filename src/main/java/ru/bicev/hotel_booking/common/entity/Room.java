package ru.bicev.hotel_booking.common.entity;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bicev.hotel_booking.common.enums.RoomStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Room {

    @Id
    private UUID id;

    private String number;

    private String type;

    @Enumerated(EnumType.STRING)
    private RoomStatus status;

}
