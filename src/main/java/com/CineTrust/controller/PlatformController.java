package com.CineTrust.controller;



import com.CineTrust.dto.PlatformCreateRequest;
import com.CineTrust.dto.PlatformResponse;
import com.CineTrust.dto.PlatformUpdateRequest;
import com.CineTrust.entity.Platform;
import com.CineTrust.entity.PlatformType;
import com.CineTrust.service.PlatformService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PlatformController {
    private final PlatformService platformService;

    @PostMapping("/admin/platforms")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PlatformResponse> createPlatform(@Valid @RequestBody PlatformCreateRequest request){
        return ResponseEntity.ok(platformService.createPlatform(request));
    }

    @GetMapping("/platforms/{id}")
    public ResponseEntity<PlatformResponse> getPlatformById(@PathVariable Long id){
        return ResponseEntity.ok(platformService.getPlatformById(id));
    }

    @PutMapping("/admin/platforms/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PlatformResponse> updatePlatform(@PathVariable Long id,
                                                           @Valid @RequestBody PlatformUpdateRequest request) {
        return ResponseEntity.ok(platformService.updatePlatform(id, request));
    }

    @DeleteMapping("/admin/platforms/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deletePlatform(@PathVariable Long id) {
        platformService.deletePlatform(id);
        return ResponseEntity.ok(Map.of("message", "Platform deleted successfully"));
    }

    @GetMapping("/platforms")
    public ResponseEntity<List<PlatformResponse>> listPlatforms(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) PlatformType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Page<PlatformResponse> result = platformService.listPlatforms(name, type, page, size, sortBy, sortDir);
        return ResponseEntity.ok(result.getContent());
    }




    
}
