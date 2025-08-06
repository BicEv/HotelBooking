package ru.bicev.hotel_booking.payment.service;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ru.bicev.hotel_booking.common.dto.PaymentDto;
import ru.bicev.hotel_booking.common.dto.PaymentRequestDto;
import ru.bicev.hotel_booking.payment.kafka.PaymentEventProducer;

@Service
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    private final PaymentEventProducer paymentEventProducer;

    public PaymentService(PaymentEventProducer paymentEventProducer) {
        this.paymentEventProducer = paymentEventProducer;
    }

    public void emulatePayment(PaymentRequestDto requestDto) {
        boolean success = ThreadLocalRandom.current().nextInt(100) < 90;
        var payment = mapFromRequest(requestDto);

        if (success) {
            logger.info("Payment success for booking:{}", requestDto.bookingId());
            paymentEventProducer.sendPaymentCompletedEvent(payment);
        } else {
            logger.info("Payment failed for booking:{}", requestDto.bookingId());
            paymentEventProducer.sendPaymentFailedEvent(payment);
        }

    }

    private PaymentDto mapFromRequest(PaymentRequestDto requestDto) {
        var payment = new PaymentDto(UUID.randomUUID(), requestDto.bookingId(), requestDto.userId(),
                requestDto.amount());
        return payment;
    }

}
