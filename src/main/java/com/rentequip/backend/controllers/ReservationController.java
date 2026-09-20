package com.rentequip.backend.controllers;

import com.rentequip.backend.dtos.request.ReservationCreateRequest;
import com.rentequip.backend.dtos.request.ReservationStatusUpdateRequest;
import com.rentequip.backend.dtos.response.AvailabilityResponse;
import com.rentequip.backend.dtos.response.PageResponse;
import com.rentequip.backend.dtos.response.ReservationResponse;
import com.rentequip.backend.enums.ReservationStatus;
import com.rentequip.backend.services.ReservationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
@Validated
public class ReservationController {

    private static final String ACTING_COMPANY_HEADER = "X-Company-Id";

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationResponse> create(@Valid @RequestBody ReservationCreateRequest request,
                                                       UriComponentsBuilder uriBuilder) {
        ReservationResponse created = reservationService.create(request);
        URI location = uriBuilder.path("/api/v1/reservations/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> findById(
            @PathVariable Long id,
            @RequestHeader(ACTING_COMPANY_HEADER) @NotNull Long actingCompanyId) {
        return ResponseEntity.ok(reservationService.findById(id, actingCompanyId));
    }

    @GetMapping
    public ResponseEntity<PageResponse<ReservationResponse>> findMine(
            @RequestHeader(ACTING_COMPANY_HEADER) @NotNull Long actingCompanyId,
            @RequestParam(defaultValue = "RENTER") ReservationRole role,
            @RequestParam(required = false) ReservationStatus status,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        PageResponse<ReservationResponse> body = role == ReservationRole.OWNER
                ? reservationService.findByOwner(actingCompanyId, status, pageable)
                : reservationService.findByRenter(actingCompanyId, status, pageable);
        return ResponseEntity.ok(body);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ReservationResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody ReservationStatusUpdateRequest request,
            @RequestHeader(ACTING_COMPANY_HEADER) @NotNull Long actingCompanyId) {
        return ResponseEntity.ok(reservationService.updateStatus(id, request, actingCompanyId));
    }

    @PostMapping("/{id}/confirmation")
    public ResponseEntity<ReservationResponse> confirm(
            @PathVariable Long id,
            @RequestHeader(ACTING_COMPANY_HEADER) @NotNull Long actingCompanyId) {
        return ResponseEntity.ok(reservationService.confirm(id, actingCompanyId));
    }

    @PostMapping("/{id}/cancellation")
    public ResponseEntity<ReservationResponse> cancel(
            @PathVariable Long id,
            @RequestParam(required = false) String reason,
            @RequestHeader(ACTING_COMPANY_HEADER) @NotNull Long actingCompanyId) {
        return ResponseEntity.ok(reservationService.cancel(id, reason, actingCompanyId));
    }

    @GetMapping("/availability")
    public ResponseEntity<AvailabilityResponse> checkAvailability(
            @RequestParam @NotNull Long equipmentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(reservationService.checkAvailability(equipmentId, startDate, endDate));
    }

    /**
     * Which side of the marketplace the caller is asking about.
     */
    public enum ReservationRole {
        RENTER,
        OWNER
    }
}
