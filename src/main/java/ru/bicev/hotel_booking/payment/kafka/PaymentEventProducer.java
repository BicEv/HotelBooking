package ru.bicev.hotel_booking.payment.kafka;

import java.time.Instant;
import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.bicev.hotel_booking.common.dto.PaymentDto;
import ru.bicev.hotel_booking.common.event.PaymentCompletedEvent;
import ru.bicev.hotel_booking.common.event.PaymentFailedEvent;

@Service
public class PaymentEventProducer {

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public PaymentEventProducer(ObjectMapper objectMapper, KafkaTemplate<String, String> kafkaTemplate) {
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendPaymentCompletedEvent(PaymentDto paymentDto) {
        var eventId = UUID.randomUUID();
        var timestamp = Instant.now();

        PaymentCompletedEvent event = new PaymentCompletedEvent(eventId, timestamp, paymentDto.bookingId(),
                paymentDto.id(), paymentDto.userId());
        String json = serializeToJson(event);
        kafkaTemplate.send("payment.completed", event.eventId().toString(), json);

    }

    public void sendPaymentFailedEvent(PaymentDto paymentDto) {
        var eventId = UUID.randomUUID();
        var timestamp = Instant.now();

        PaymentFailedEvent event = new PaymentFailedEvent(eventId, timestamp, paymentDto.bookingId(), "Payment failed");
        String json = serializeToJson(event);
        kafkaTemplate.send("payment.failed", event.eventId().toString(), json);
    }

    private String serializeToJson(PaymentCompletedEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Serialization error", e);
        }
    }

    private String serializeToJson(PaymentFailedEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Serialization error", e);
        }
    }

}
