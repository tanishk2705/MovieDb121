package com.CineTrust.service;

import com.CineTrust.dto.MovieAvailabilityRequest;
import com.CineTrust.dto.MovieAvailabilityResponse;
import com.CineTrust.entity.Movie;
import com.CineTrust.entity.MovieAvailability;
import com.CineTrust.entity.Platform;
import com.CineTrust.entity.Region;
import com.CineTrust.exception.ResourceNotFoundException;
import com.CineTrust.repository.MovieAvailabilityRepository;
import com.CineTrust.repository.MovieRepository;
import com.CineTrust.repository.PlatformRepository;
import com.CineTrust.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MovieAvailabilityService {

    private final MovieRepository movieRepository;
    private final PlatformRepository platformRepository;
    private final RegionRepository regionRepository;
    private final MovieAvailabilityRepository movieAvailabilityRepository;


    public MovieAvailabilityResponse linkAvailability(Long movieId, MovieAvailabilityRequest request) {

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + movieId));

        Platform platform = platformRepository.findById(request.getPlatformId())
                .orElseThrow(() -> new ResourceNotFoundException("Platform not found with id: " + request.getPlatformId()));

        Region region = regionRepository.findById(request.getRegionId())
                .orElseThrow(() -> new ResourceNotFoundException("Region not found with id: " + request.getRegionId()));

        boolean exists = movieAvailabilityRepository.existsByMovieAndPlatformAndRegion(movie, platform, region);
        if (exists) {
            throw new IllegalArgumentException("This movie is already linked to the specified platform and region.");
        }

        MovieAvailability availability = MovieAvailability.builder()
                .movie(movie)
                .platform(platform)
                .region(region)
                .availabilityType(request.getAvailabilityType())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .url(request.getUrl())
                .build();

        MovieAvailability saved = movieAvailabilityRepository.save(availability);
        return mapToResponse(saved);
    }


    public void unlinkAvailability(Long movieId, Long availabilityId) {
        MovieAvailability availability = movieAvailabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new ResourceNotFoundException("Availability not found with id: " + availabilityId));

        if (!availability.getMovie().getId().equals(movieId)) {
            throw new IllegalArgumentException("This availability does not belong to the given movie.");
        }

        movieAvailabilityRepository.delete(availability);
    }


    private MovieAvailabilityResponse mapToResponse(MovieAvailability availability) {
        return MovieAvailabilityResponse.builder()
                .id(availability.getId())
                .movieId(availability.getMovie().getId())
                .movieTitle(availability.getMovie().getTitle())
                .platformName(availability.getPlatform().getName())
                .regionCode(availability.getRegion().getCode())
                .availabilityType(availability.getAvailabilityType())
                .startDate(availability.getStartDate())
                .endDate(availability.getEndDate())
                .url(availability.getUrl())
                .build();
    }
}

