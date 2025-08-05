package ru.bicev.hotel_booking.payment.service;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;

import ru.bicev.hotel_booking.common.dto.PaymentDto;
import ru.bicev.hotel_booking.common.dto.PaymentRequestDto;
import ru.bicev.hotel_booking.payment.kafka.PaymentEventProducer;

@Service
public class PaymentService {

    private final PaymentEventProducer paymentEventProducer;

    public PaymentService(PaymentEventProducer paymentEventProducer) {
        this.paymentEventProducer = paymentEventProducer;
    }

    public void emulatePayment(PaymentRequestDto requestDto) {
        boolean success = ThreadLocalRandom.current().nextInt(100) < 90;
        var payment = mapFromRequest(requestDto);

        if (success) {
            paymentEventProducer.sendPaymentCompletedEvent(payment);
        } else {
            paymentEventProducer.sendPaymentFailedEvent(payment);
        }

    }

    private PaymentDto mapFromRequest(PaymentRequestDto requestDto) {
        var payment = new PaymentDto(UUID.randomUUID(), requestDto.bookingId(), requestDto.userId(),
                requestDto.amount());
        return payment;
    }

}
