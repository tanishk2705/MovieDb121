package com.CineTrust.controller;

import com.CineTrust.dto.RegionCreateRequest;
import com.CineTrust.dto.RegionResponse;
import com.CineTrust.service.RegionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RegionController {
    private final RegionService regionService;

    @PostMapping("admin/regions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RegionResponse> createRegion(@Valid @RequestBody RegionCreateRequest request) {
        RegionResponse response = regionService.createRegion(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/regions/{id}")
    public ResponseEntity<RegionResponse> getRegionById(@PathVariable Long id) {
        RegionResponse response = regionService.getRegionById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/regions")
    public ResponseEntity<?> getAllRegions() {

        Map<String, Object> response = Map.of(
                "status", "Success",
                "data", regionService.getAllRegions()
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/soft-delete-region/{id}")
    public ResponseEntity<?> softDelete(@PathVariable Long id){
        regionService.softDeleteRegion(id);
        return ResponseEntity.noContent().build();
    }
}

