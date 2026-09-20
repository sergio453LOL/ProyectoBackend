package com.rentequip.backend.dtos.response;

import java.time.LocalDate;

public record AvailabilityResponse(
        Long equipmentId,
        LocalDate startDate,
        LocalDate endDate,
        boolean available
) {
}
