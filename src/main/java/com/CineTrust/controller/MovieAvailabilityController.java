package com.CineTrust.controller;

import com.CineTrust.dto.MovieAvailabilityRequest;
import com.CineTrust.dto.MovieAvailabilityResponse;
import com.CineTrust.service.MovieAvailabilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class MovieAvailabilityController {

    private final MovieAvailabilityService movieAvailabilityService;

    @PostMapping("/movies/{movieId}/availability")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovieAvailabilityResponse> linkMovieAvailability(
            @PathVariable Long movieId,
            @Valid @RequestBody MovieAvailabilityRequest request) {

        MovieAvailabilityResponse response = movieAvailabilityService.linkAvailability(movieId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/movies/{movieId}/availability/{availabilityId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> unlinkMovieAvailability(
            @PathVariable Long movieId,
            @PathVariable Long availabilityId) {

        movieAvailabilityService.unlinkAvailability(movieId, availabilityId);
        return ResponseEntity.noContent().build();
    }
}


