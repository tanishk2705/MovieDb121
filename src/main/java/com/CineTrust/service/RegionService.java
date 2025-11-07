package com.CineTrust.service;

import com.CineTrust.dto.RegionCreateRequest;
import com.CineTrust.dto.RegionResponse;
import com.CineTrust.entity.Platform;
import com.CineTrust.entity.Region;
import com.CineTrust.entity.Status;
import com.CineTrust.exception.ResourceNotFoundException;
import com.CineTrust.repository.PlatformRepository;
import com.CineTrust.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegionService {
    private final RegionRepository regionRepository;
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Transactional
    public RegionResponse createRegion(RegionCreateRequest request) {
        if (regionRepository.existsByCodeIgnoreCase(request.getCode())) {
            throw new IllegalArgumentException("Region with code '" + request.getCode() + "' already exists.");
        }

        Region region = Region.builder()
                .name(request.getName())
                .code(request.getCode().toUpperCase())
                .build();

        Region saved = regionRepository.save(region);
        return mapToResponse(saved);
    }


    public RegionResponse getRegionById(Long id) {
        Region region = regionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Region not found with id: " + id));
        return mapToResponse(region);
    }


    public List<RegionResponse> getAllRegions() {
        return regionRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private RegionResponse mapToResponse(Region region) {
        return RegionResponse.builder()
                .id(region.getId())
                .name(region.getName())
                .code(region.getCode())
                .createdAt(formatter.format(region.getCreatedAt()))
                .updatedAt(formatter.format(region.getUpdatedAt()))
                .build();
    }


    public void softDeleteRegion(Long regionId){
        Optional<Region> region = regionRepository.findById(regionId);
          region.get().setStatusCheck(Status.SUSPENDED);
        regionRepository.save(region.get());
    }
}
