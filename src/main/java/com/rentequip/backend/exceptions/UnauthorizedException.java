package com.rentequip.backend.exceptions;

import org.springframework.http.HttpStatus;

/**
 * The caller could not be identified. Maps to 401 Unauthorized.
 */
public class UnauthorizedException extends RentEquipException {

    public UnauthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
    }
}
