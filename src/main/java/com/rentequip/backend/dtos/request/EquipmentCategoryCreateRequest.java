package com.rentequip.backend.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EquipmentCategoryCreateRequest(

        @NotBlank @Size(min = 2, max = 80)
        String name,

        @Size(max = 255)
        String description
) {
}
