package ru.bicev.hotel_booking.booking.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.bicev.hotel_booking.booking.service.BookingService;
import ru.bicev.hotel_booking.common.event.PaymentCompletedEvent;
import ru.bicev.hotel_booking.common.event.PaymentFailedEvent;

@Component
public class PaymentEventListener {

    private static final Logger logger = LoggerFactory.getLogger(PaymentEventListener.class);
    private final ObjectMapper objectMapper;
    private final BookingService bookingService;

    public PaymentEventListener(ObjectMapper objectMapper, BookingService bookingService) {
        this.objectMapper = objectMapper;
        this.bookingService = bookingService;
    }

    @KafkaListener(topics = "payment.completed", groupId = "payment-completed-consumer")
    public void handlePaymentConfirmed(String message) {
        logger.info("Received payment.completed message: {}", message);
        try {
            PaymentCompletedEvent event = objectMapper.readValue(message, PaymentCompletedEvent.class);
            bookingService.confirmBookingStatus(event.bookingId());
        } catch (JsonProcessingException e) {
            logger.error("payment.completed deserialization error: {}", e.getMessage());
            throw new RuntimeException("Deserialization error", e);
        }
    }

    @KafkaListener(topics = "payment.failed", groupId = "payment-failed-consumer")
    public void handlePaymentCancelled(String message) {
        logger.info("Received payment.failed message: {}", message);
        try {
            PaymentFailedEvent event = objectMapper.readValue(message, PaymentFailedEvent.class);
            bookingService.cancelBooking(event.bookingId());
        } catch (JsonProcessingException e) {
            logger.error("payment.failed deserialization error: {}", e.getMessage());
            throw new RuntimeException("Deserialization error", e);
        }
    }

}
