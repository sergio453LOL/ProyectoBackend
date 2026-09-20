package com.rentequip.backend.exceptions;

import org.springframework.http.HttpStatus;

/**
 * The caller is known but does not own the resource it is trying to act on. Maps to 403 Forbidden.
 */
public class ForbiddenOperationException extends RentEquipException {

    public ForbiddenOperationException(String message) {
        super(message, HttpStatus.FORBIDDEN, "FORBIDDEN_OPERATION");
    }

    public static ForbiddenOperationException notOwner(String resource, Long resourceId) {
        return new ForbiddenOperationException(
                "The acting company does not own %s %d".formatted(resource, resourceId));
    }
}
