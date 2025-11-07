package com.CineTrust.service;

import com.CineTrust.dto.PlatformCreateRequest;
import com.CineTrust.dto.PlatformResponse;
import com.CineTrust.dto.PlatformUpdateRequest;
import com.CineTrust.entity.Platform;
import com.CineTrust.entity.PlatformType;
import com.CineTrust.exception.ResourceNotFoundException;
import com.CineTrust.repository.PlatformRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;

@Service
@RequiredArgsConstructor
public class PlatformService {
    private final PlatformRepository platformRepository;
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Transactional
    public PlatformResponse createPlatform(PlatformCreateRequest request){
        if (platformRepository.existsByNameIgnoreCase(request.getName())){
            throw new IllegalArgumentException("Platform with this name already exists.");
        }

        Platform platform = Platform.builder()
                .name(request.getName())
                .type(request.getType())
                .website(request.getWebsite())
                .build();

        Platform saved = platformRepository.save(platform);
        return mapToResponse(saved);


    }


    public PlatformResponse getPlatformById(Long id) {
        Platform platform = platformRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Platform not found with id: " + id));
        return mapToResponse(platform);
    }

    @Transactional
    public PlatformResponse updatePlatform(Long id, PlatformUpdateRequest request) {
        Platform platform = platformRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Platform not found with id: " + id));

        if (request.getName() != null) platform.setName(request.getName());
        if (request.getType() != null) platform.setType(request.getType());
        if (request.getWebsite() != null) platform.setWebsite(request.getWebsite());

        Platform updated = platformRepository.save(platform);
        return mapToResponse(updated);
    }

    @Transactional
    public void deletePlatform(Long id) {
        Platform platform = platformRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Platform not found with id: " + id));

        // If you have cascading deletes (like Platform -> MoviePlatform), make sure JPA relationships have cascade = CascadeType.REMOVE
        platformRepository.delete(platform);
    }


    public Page<PlatformResponse> listPlatforms(String name, PlatformType type, int page, int size, String sortBy, String sortDir) {
        Pageable pageable = PageRequest.of(page, size,
                sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending());

        Page<Platform> platforms;

        if (name != null && type != null) {
            platforms = platformRepository.findByNameContainingIgnoreCaseAndType(name, type, pageable);
        } else if (name != null) {
            platforms = platformRepository.findByNameContainingIgnoreCase(name, pageable);
        } else if (type != null) {
            platforms = platformRepository.findByType(type, pageable);
        } else {
            platforms = platformRepository.findAll(pageable);
        }

        return platforms.map(this::mapToResponse);
    }


    private PlatformResponse mapToResponse(Platform p){
        return PlatformResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .type(p.getType())
                .website(p.getWebsite())
                .createdAt(formatter.format(p.getCreatedAt()))
                .updatedAt(formatter.format(p.getUpdatedAt()))
                .build();
    }

}
