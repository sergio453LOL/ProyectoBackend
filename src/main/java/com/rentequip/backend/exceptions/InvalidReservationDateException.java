package com.rentequip.backend.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidReservationDateException extends RentEquipException {

    public InvalidReservationDateException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "INVALID_RESERVATION_DATES");
    }
}
