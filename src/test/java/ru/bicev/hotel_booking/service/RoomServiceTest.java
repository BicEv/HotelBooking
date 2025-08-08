package ru.bicev.hotel_booking.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.bicev.hotel_booking.common.dto.CreateRoomDto;
import ru.bicev.hotel_booking.common.dto.RoomDto;
import ru.bicev.hotel_booking.common.entity.Room;
import ru.bicev.hotel_booking.common.exception.RoomNotFoundException;
import ru.bicev.hotel_booking.room.repository.RoomRepository;
import ru.bicev.hotel_booking.room.service.RoomService;

@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private RoomService roomService;

    private UUID uuid = UUID.randomUUID();
    Room testRoom = new Room(uuid, "1408", "Regular");

    CreateRoomDto testCreateRoomDto = new CreateRoomDto("1408", "Regular");
    RoomDto testRoomDto = new RoomDto(uuid, "1408", "Regular");

    @Test
    public void createRoomSuccess() {
        when(roomRepository.save(any(Room.class))).thenReturn(testRoom);

        RoomDto savedRoom = roomService.createRoom(testCreateRoomDto);

        assertEquals(uuid, savedRoom.id());
        assertEquals(testRoom.getNumber(), savedRoom.number());
        assertEquals(testRoom.getType(), savedRoom.type());
    }

    @Test
    public void getRoomByUUIDSuccess() {
        when(roomRepository.findById(uuid)).thenReturn(Optional.of(testRoom));

        RoomDto fountRoomDto = roomService.getRoomByUUID(uuid);

        assertEquals(uuid, fountRoomDto.id());
        assertEquals("1408", fountRoomDto.number());
        assertEquals("Regular", fountRoomDto.type());
    }

    @Test
    public void getRoomByUUIDException() {
        when(roomRepository.findById(uuid)).thenReturn(Optional.empty());

        assertThrows(RoomNotFoundException.class, () -> roomService.getRoomByUUID(uuid));

    }

    @Test
    public void updateRoomSuccess() {
        RoomDto dto = new RoomDto(uuid, "101", "Deluxe");

        when(roomRepository.findById(uuid)).thenReturn(Optional.of(testRoom));
        when(roomRepository.save(any(Room.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RoomDto result = roomService.updateRoom(dto);

        assertEquals(dto.id(), result.id());
        assertEquals(dto.number(), result.number());
        assertEquals(dto.type(), result.type());

        verify(roomRepository).findById(uuid);
    }

    @Test
    public void updateRoomException() {
        RoomDto dto = new RoomDto(uuid, "101", "Deluxe");

        when(roomRepository.findById(uuid)).thenReturn(Optional.empty());

        assertThrows(RoomNotFoundException.class, () -> roomService.updateRoom(dto));
    }

    @Test
    public void deleteRoomSuccess(){
        roomService.deleteRoom(uuid);

        verify(roomRepository).deleteById(uuid);
    }

}
