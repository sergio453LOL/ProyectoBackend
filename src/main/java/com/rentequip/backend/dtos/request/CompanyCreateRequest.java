package com.rentequip.backend.dtos.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CompanyCreateRequest(

        @NotBlank @Size(min = 2, max = 150)
        String name,

        @NotBlank @Pattern(regexp = "^\\d{11}$", message = "Tax ID (RUC) must contain exactly 11 digits")
        String taxId,

        @NotBlank @Email @Size(max = 150)
        String email,

        @Size(max = 20)
        String phone,

        @Size(max = 255)
        String address,

        @NotBlank @Size(max = 100)
        String city,

        @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") @Digits(integer = 2, fraction = 7)
        BigDecimal latitude,

        @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") @Digits(integer = 3, fraction = 7)
        BigDecimal longitude
) {
}
