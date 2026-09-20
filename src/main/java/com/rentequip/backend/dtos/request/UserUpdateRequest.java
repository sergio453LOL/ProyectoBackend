package com.rentequip.backend.dtos.request;

import com.rentequip.backend.enums.UserRole;
import jakarta.validation.constraints.Size;

/**
 * Partial update: a null field means "keep the current value". Email and password are changed through dedicated flows.
 */
public record UserUpdateRequest(

        @Size(min = 1, max = 80)
        String firstName,

        @Size(min = 1, max = 80)
        String lastName,

        @Size(max = 20)
        String phone,

        UserRole role
) {
}
