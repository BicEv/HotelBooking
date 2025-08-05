package ru.bicev.hotel_booking.payment.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.bicev.hotel_booking.common.event.BookingCreatedEvent;
import ru.bicev.hotel_booking.common.util.EventMapper;
import ru.bicev.hotel_booking.payment.service.PaymentService;

@Component
public class BookingEventListener {

    private final PaymentService paymentService;
    private final ObjectMapper objectMapper;

    public BookingEventListener(PaymentService paymentService, ObjectMapper objectMapper) {
        this.paymentService = paymentService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "booking.created", groupId = "booking-created-consumer")
    public void handleBookingCreated(String message) {
        try {
            BookingCreatedEvent event = objectMapper.readValue(message, BookingCreatedEvent.class);
            var paymentRequest = EventMapper.mapBookingCreatedToPaymentRequest(event);
            paymentService.emulatePayment(paymentRequest);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Deserialization error", e);

        }

    }

}
