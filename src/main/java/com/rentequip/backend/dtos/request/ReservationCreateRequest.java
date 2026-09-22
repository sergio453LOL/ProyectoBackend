package com.rentequip.backend.dtos.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * The renting company is never part of the payload: it is taken from the authenticated token, so a
 * caller cannot book equipment in someone else's name.
 */
public record ReservationCreateRequest(

        @NotNull
        Long equipmentId,

        @NotNull @FutureOrPresent
        LocalDate startDate,

        @NotNull @FutureOrPresent
        LocalDate endDate,

        @Size(max = 500)
        String notes
) {

    @AssertTrue(message = "End date must be on or after start date")
    public boolean isDateRangeValid() {
        return startDate == null || endDate == null || !endDate.isBefore(startDate);
    }
}
