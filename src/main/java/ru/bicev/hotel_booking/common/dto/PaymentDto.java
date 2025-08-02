package ru.bicev.hotel_booking.common.dto;

import java.math.BigDecimal;
import java.util.UUID;

import ru.bicev.hotel_booking.common.enums.PaymentStatus;

public record PaymentDto(UUID id, UUID bookingId, UUID userId, BigDecimal amount, PaymentStatus status) {

}
