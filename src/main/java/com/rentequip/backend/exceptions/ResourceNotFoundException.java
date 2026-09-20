package com.rentequip.backend.exceptions;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends RentEquipException {

    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND");
    }

    public static ResourceNotFoundException of(String resource, Object identifier) {
        return new ResourceNotFoundException("%s not found with id %s".formatted(resource, identifier));
    }
}
