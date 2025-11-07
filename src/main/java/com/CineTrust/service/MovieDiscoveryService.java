package com.CineTrust.service;

import com.CineTrust.dto.MovieDiscoveryResponse;
import com.CineTrust.entity.AvailabilityType;
import com.CineTrust.entity.MovieAvailability;
import com.CineTrust.repository.MovieAvailabilityRepository;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MovieDiscoveryService {

    private static final Logger logger = LoggerFactory.getLogger(MovieDiscoveryService.class);
    private final MovieAvailabilityRepository movieAvailabilityRepository;

    // ========================= Filter & Find =========================
    public Page<MovieDiscoveryResponse> getAvailableMovies(
            String regionCode,
            String platformName,
            AvailabilityType availabilityType,
            int page,
            int size,
            String sortBy,
            String sortDir
    ) {
        logger.info("Fetching available movies with filters - region: {}, platform: {}, type: {}",
                regionCode, platformName, availabilityType);

        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("Invalid pagination parameters");
        }

        Specification<MovieAvailability> spec = (root, query, cb) -> cb.conjunction();

        if (regionCode != null && !regionCode.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.join("region", JoinType.INNER).get("code"), regionCode));
        }

        if (platformName != null && !platformName.isEmpty()) {
            spec = spec.and((root, query, cb) -> {
                Expression<String> platformNameField = cb.lower(root.join("platform", JoinType.INNER).get("name"));
                return cb.like(platformNameField, "%" + platformName.toLowerCase() + "%");
            });
        }

        if (availabilityType != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("availabilityType"), availabilityType));
        }

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<MovieAvailability> result = movieAvailabilityRepository.findAll(spec, pageable);

        logger.info("Found {} movie availability records", result.getTotalElements());

        return result.map(this::mapToResponse);
    }

    private MovieDiscoveryResponse mapToResponse(MovieAvailability ma) {
        return MovieDiscoveryResponse.builder()
                .id(ma.getId())
                .movieId(ma.getMovie().getId())
                .movieTitle(ma.getMovie().getTitle())
                .platformName(ma.getPlatform().getName())
                .regionCode(ma.getRegion().getCode())
                .availabilityType(ma.getAvailabilityType())
                .startDate(ma.getStartDate())
                .endDate(ma.getEndDate())
                .url(ma.getUrl())
                .build();
    }
}