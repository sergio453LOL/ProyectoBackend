package com.rentequip.backend.dtos.response;

import com.rentequip.backend.enums.UserRole;

/**
 * Credentials handed to the client after a successful login, registration or refresh.
 */
public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresInSeconds,
        Long userId,
        String email,
        UserRole role,
        Long companyId,
        String companyName
) {

    public static final String BEARER = "Bearer";
}
