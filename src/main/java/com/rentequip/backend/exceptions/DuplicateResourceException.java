package com.rentequip.backend.exceptions;

import org.springframework.http.HttpStatus;

public class DuplicateResourceException extends RentEquipException {

    public DuplicateResourceException(String message) {
        super(message, HttpStatus.CONFLICT, "DUPLICATE_RESOURCE");
    }

    public static DuplicateResourceException of(String resource, String field, Object value) {
        return new DuplicateResourceException("%s already exists with %s '%s'".formatted(resource, field, value));
    }
}
