package com.rentequip.backend.dtos.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Onboarding of a brand new tenant: the company and the administrator that will manage it are created
 * together, because a company without an administrator could never be operated.
 */
public record RegisterRequest(

        @NotNull @Valid
        CompanyCreateRequest company,

        @NotBlank @Size(max = 80)
        String firstName,

        @NotBlank @Size(max = 80)
        String lastName,

        @NotBlank @Email @Size(max = 150)
        String email,

        @NotBlank
        @Size(min = 8, max = 64)
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
                message = "Password must contain at least one lowercase letter, one uppercase letter and one digit"
        )
        String password,

        @Size(max = 20)
        String phone
) {
}
