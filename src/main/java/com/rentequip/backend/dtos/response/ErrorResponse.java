package com.rentequip.backend.dtos.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Standard error payload returned by the global exception handler for every failed request.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String code,
        String message,
        String path,
        List<FieldValidationError> details
) {

    public record FieldValidationError(String field, Object rejectedValue, String message) {
    }
}
