package com.rentequip.backend.dtos.response;

import com.rentequip.backend.enums.PaymentStatus;
import com.rentequip.backend.enums.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
        Long id,
        Long reservationId,
        BigDecimal amount,
        String currency,
        PaymentType type,
        PaymentStatus status,
        String providerReference,
        LocalDateTime paidAt,
        String failureReason,
        LocalDateTime createdAt
) {
}
