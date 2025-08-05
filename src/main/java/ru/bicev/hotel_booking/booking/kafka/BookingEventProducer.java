package ru.bicev.hotel_booking.booking.kafka;

import java.time.Instant;
import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.bicev.hotel_booking.common.entity.Booking;
import ru.bicev.hotel_booking.common.event.BookingCreatedEvent;
import ru.bicev.hotel_booking.common.util.EventMapper;

@Service
public class BookingEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public BookingEventProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendBookingCreatedEvent(Booking booking) {
        var eventId = UUID.randomUUID();
        var timestamp = Instant.now();
        BookingCreatedEvent event = EventMapper.mapBookingToEvent(booking, eventId, timestamp);
        String json = serializeToJson(event);
        kafkaTemplate.send("booking.created", eventId.toString(), json);
    }

    private String serializeToJson(BookingCreatedEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Serialization error", e);
        }
    }

}
