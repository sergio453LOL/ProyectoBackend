package com.rentequip.backend.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Renders authentication and authorization failures with the same ErrorResponse shape the global
 * exception handler produces, so a 401 from the filter chain is not a different contract from a 403
 * raised inside a service.
 */
@Component
public class SecurityErrorWriter {

    private static final String TEMPLATE = """
            {"timestamp":"%s","status":%d,"error":"%s","code":"%s","message":"%s","path":"%s"}""";

    public void write(HttpServletRequest request, HttpServletResponse response,
                      HttpStatus status, String code, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(TEMPLATE.formatted(
                LocalDateTime.now(), status.value(), status.getReasonPhrase(),
                code, message, request.getRequestURI()));
    }
}
