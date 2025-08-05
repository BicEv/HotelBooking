package ru.bicev.hotel_booking.booking.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.bicev.hotel_booking.booking.service.BookingService;
import ru.bicev.hotel_booking.common.event.PaymentCompletedEvent;
import ru.bicev.hotel_booking.common.event.PaymentFailedEvent;

@Component
public class PaymentEventListener {

    private final ObjectMapper objectMapper;
    private final BookingService bookingService;

    public PaymentEventListener(ObjectMapper objectMapper, BookingService bookingService) {
        this.objectMapper = objectMapper;
        this.bookingService = bookingService;
    }

    @KafkaListener(topics = "payment.confirmed", groupId = "booking")
    public void handlePaymentConfirmed(String message) {
        try {
            PaymentCompletedEvent event = objectMapper.readValue(message, PaymentCompletedEvent.class);
            bookingService.confirmBookingStatus(event.bookingId());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Deserialization error", e);
        }
    }

    @KafkaListener(topics = "payment.failed", groupId = "booking")
    public void handlePaymentCancelled(String message) {
        try {
            PaymentFailedEvent event = objectMapper.readValue(message, PaymentFailedEvent.class);
            bookingService.cancelBooking(event.bookingId());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Deserialization error", e);
        }
    }

}
