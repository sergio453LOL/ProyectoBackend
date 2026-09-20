package com.rentequip.backend.exceptions;

import org.springframework.http.HttpStatus;

import java.time.LocalDate;

/**
 * Raised when the requested date range collides with a reservation that already blocks the calendar.
 * Maps to 409 Conflict so the client can offer the renter a different window.
 */
public class OverbookingException extends RentEquipException {

    public OverbookingException(String message) {
        super(message, HttpStatus.CONFLICT, "EQUIPMENT_NOT_AVAILABLE");
    }

    public static OverbookingException of(Long equipmentId, LocalDate startDate, LocalDate endDate) {
        return new OverbookingException(
                "Equipment %d is already booked between %s and %s".formatted(equipmentId, startDate, endDate));
    }
}
