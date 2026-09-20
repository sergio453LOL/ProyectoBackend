package com.rentequip.backend.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Root of the application exception hierarchy. Every domain failure carries the HTTP status and the
 * machine readable code that the global handler turns into an ErrorResponse.
 */
public abstract class RentEquipException extends RuntimeException {

    private final HttpStatus status;
    private final String errorCode;

    protected RentEquipException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
