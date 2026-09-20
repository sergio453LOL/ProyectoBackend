package com.rentequip.backend.controllers;

import com.rentequip.backend.dtos.request.ReviewCreateRequest;
import com.rentequip.backend.dtos.response.ReviewResponse;
import com.rentequip.backend.services.ReviewService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Validated
public class ReviewController {

    private static final String ACTING_COMPANY_HEADER = "X-Company-Id";

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponse> create(
            @Valid @RequestBody ReviewCreateRequest request,
            @RequestHeader(ACTING_COMPANY_HEADER) @NotNull Long actingCompanyId,
            UriComponentsBuilder uriBuilder) {
        ReviewResponse created = reviewService.create(request, actingCompanyId);
        URI location = uriBuilder.path("/api/v1/reviews/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.findById(id));
    }

    @GetMapping(params = "reservationId")
    public ResponseEntity<ReviewResponse> findByReservation(@RequestParam Long reservationId) {
        return ResponseEntity.ok(reviewService.findByReservation(reservationId));
    }

    @GetMapping("/ratings")
    public ResponseEntity<Map<String, Object>> findAverageRating(@RequestParam Long equipmentId) {
        return ResponseEntity.ok(Map.of(
                "equipmentId", equipmentId,
                "averageRating", reviewService.findAverageRating(equipmentId)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                        @RequestHeader(ACTING_COMPANY_HEADER) @NotNull Long actingCompanyId) {
        reviewService.delete(id, actingCompanyId);
        return ResponseEntity.noContent().build();
    }
}
