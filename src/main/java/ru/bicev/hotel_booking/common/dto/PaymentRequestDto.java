package ru.bicev.hotel_booking.common.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRequestDto(UUID bookingId, UUID userId, BigDecimal amount) {

}
