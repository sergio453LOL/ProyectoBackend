package com.rentequip.backend.dtos.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReviewCreateRequest(

        @NotNull
        Long reservationId,

        @NotNull @Min(1) @Max(5)
        Integer rating,

        @Min(1) @Max(5)
        Integer conditionRating,

        @Size(max = 1000)
        String comment
) {
}
