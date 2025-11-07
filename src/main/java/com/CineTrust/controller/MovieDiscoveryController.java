package com.CineTrust.controller;

import com.CineTrust.dto.MovieDiscoveryResponse;
import com.CineTrust.entity.AvailabilityType;
import com.CineTrust.service.MovieDiscoveryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MovieDiscoveryController {
    private final MovieDiscoveryService movieDiscoveryService;

    @Operation(summary = "Fetch all available movies (filter by region, platform, and availability type)")
    @GetMapping("/movies/available")
    public ResponseEntity<Page<MovieDiscoveryResponse>> getAvailableMovies(
            @RequestParam(required = false) String regionCode,
            @RequestParam(required = false) String platformName,
            @RequestParam(required = false) AvailabilityType availabilityType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Page<MovieDiscoveryResponse> response = movieDiscoveryService.getAvailableMovies(
                regionCode, platformName, availabilityType, page, size, sortBy, sortDir
        );
        return ResponseEntity.ok(response);
    }
}
