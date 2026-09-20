package com.rentequip.backend.exceptions;

import com.rentequip.backend.enums.ReservationStatus;
import org.springframework.http.HttpStatus;

public class InvalidStatusTransitionException extends RentEquipException {

    public InvalidStatusTransitionException(String message) {
        super(message, HttpStatus.CONFLICT, "INVALID_STATUS_TRANSITION");
    }

    public static InvalidStatusTransitionException of(ReservationStatus from, ReservationStatus to) {
        return new InvalidStatusTransitionException(
                "A reservation cannot move from %s to %s".formatted(from, to));
    }
}
