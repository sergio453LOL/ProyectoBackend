package com.rentequip.backend.exceptions;

import org.springframework.http.HttpStatus;

/**
 * A syntactically valid request that breaks a business rule (renting your own equipment, reviewing a
 * reservation that never finished, and so on).
 */
public class InvalidOperationException extends RentEquipException {

    public InvalidOperationException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "INVALID_OPERATION");
    }
}
