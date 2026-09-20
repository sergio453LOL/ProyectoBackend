package com.rentequip.backend.dtos.request;

import com.rentequip.backend.enums.ReservationStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReservationStatusUpdateRequest(

        @NotNull
        ReservationStatus status,

        @Size(max = 500)
        String reason
) {
}
