package com.rentequip.backend.controllers;

import com.rentequip.backend.dtos.request.ReviewCreateRequest;
import com.rentequip.backend.dtos.response.EquipmentRatingResponse;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Validated
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponse> create(
            @Valid @RequestBody ReviewCreateRequest request,
            UriComponentsBuilder uriBuilder) {
        ReviewResponse created = reviewService.create(request);
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
    public ResponseEntity<EquipmentRatingResponse> findAverageRating(@RequestParam Long equipmentId) {
        return ResponseEntity.ok(reviewService.findAverageRating(equipmentId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reviewService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
