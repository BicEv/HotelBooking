package ru.bicev.hotel_booking.payment.kafka;

import java.time.Instant;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.bicev.hotel_booking.common.dto.PaymentDto;
import ru.bicev.hotel_booking.common.event.PaymentCompletedEvent;
import ru.bicev.hotel_booking.common.event.PaymentFailedEvent;

@Service
public class PaymentEventProducer {

    private static final Logger logger = LoggerFactory.getLogger(PaymentEventProducer.class);
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
        logger.info("PaymentCompletedEvent sent: {}", json);
    }

    public void sendPaymentFailedEvent(PaymentDto paymentDto) {
        var eventId = UUID.randomUUID();
        var timestamp = Instant.now();

        PaymentFailedEvent event = new PaymentFailedEvent(eventId, timestamp, paymentDto.bookingId(), "Payment failed");
        String json = serializeToJson(event);
        kafkaTemplate.send("payment.failed", event.eventId().toString(), json);
        logger.info("PaymentFailedEvent sent: {}", json);
    }

    private String serializeToJson(PaymentCompletedEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            logger.info("PaymentCompletedEvent serialization exception:{}", e.getMessage());
            throw new RuntimeException("Serialization error", e);
        }
    }

    private String serializeToJson(PaymentFailedEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            logger.info("PaymentFailedEvent serialization exception:{}", e.getMessage());
            throw new RuntimeException("Serialization error", e);
        }
    }

}
