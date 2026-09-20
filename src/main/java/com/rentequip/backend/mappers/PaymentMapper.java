package com.rentequip.backend.mappers;

import com.rentequip.backend.dtos.response.PaymentResponse;
import com.rentequip.backend.entities.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getReservation().getId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getType(),
                payment.getStatus(),
                payment.getProviderReference(),
                payment.getPaidAt(),
                payment.getFailureReason(),
                payment.getCreatedAt()
        );
    }
}
