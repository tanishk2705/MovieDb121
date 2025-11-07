package com.CineTrust.service;

import com.CineTrust.dto.MovieAvailabilityRequest;
import com.CineTrust.dto.MovieAvailabilityResponse;
import com.CineTrust.entity.*;
import com.CineTrust.exception.ResourceNotFoundException;
import com.CineTrust.repository.MovieAvailabilityRepository;
import com.CineTrust.repository.MovieRepository;
import com.CineTrust.repository.PlatformRepository;
import com.CineTrust.repository.RegionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MovieAvailabilityServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private PlatformRepository platformRepository;

    @Mock
    private RegionRepository regionRepository;

    @Mock
    private MovieAvailabilityRepository movieAvailabilityRepository;

    @InjectMocks
    private MovieAvailabilityService movieAvailabilityService;

    private Movie movie;
    private Platform platform;
    private Region region;
    private MovieAvailability availability;
    private MovieAvailabilityRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        movie = Movie.builder()
                .title("Inception")
                .build();
        movie.setId(1L);

        platform = Platform.builder()
                .name("Netflix")
                .build();
        movie.setId(1L);

        region = Region.builder()
                .name("North America")
                .code("NA")
                .build();
        region.setId(3L);

        request = new MovieAvailabilityRequest();
        request.setPlatformId(2L);
        request.setRegionId(3L);
        request.setAvailabilityType(AvailabilityType.STREAMING);
        request.setStartDate(new Date());
        request.setEndDate(new Date());
        request.setUrl("https://www.netflix.com/inception");

        availability = MovieAvailability.builder()
                .movie(movie)
                .platform(platform)
                .region(region)
                .availabilityType(AvailabilityType.STREAMING)
                .startDate(new Date())
                .endDate(new Date())
                .url("https://www.netflix.com/inception")
                .build();
        availability.setId(10L);
    }


    @Test
    void testLinkAvailability_Success() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(platformRepository.findById(2L)).thenReturn(Optional.of(platform));
        when(regionRepository.findById(3L)).thenReturn(Optional.of(region));
        when(movieAvailabilityRepository.existsByMovieAndPlatformAndRegion(movie, platform, region)).thenReturn(false);
        when(movieAvailabilityRepository.save(any(MovieAvailability.class))).thenReturn(availability);

        MovieAvailabilityResponse response = movieAvailabilityService.linkAvailability(1L, request);

        assertNotNull(response);
        assertEquals(1L, response.getMovieId());
        assertEquals("Netflix", response.getPlatformName());
        assertEquals("NA", response.getRegionCode());
        verify(movieAvailabilityRepository).save(any(MovieAvailability.class));
    }


    @Test
    void testLinkAvailability_MovieNotFound() {
        when(movieRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> movieAvailabilityService.linkAvailability(1L, request));

        verify(movieRepository).findById(1L);
    }


    @Test
    void testLinkAvailability_PlatformNotFound() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(platformRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> movieAvailabilityService.linkAvailability(1L, request));

        verify(platformRepository).findById(2L);
    }


    @Test
    void testLinkAvailability_RegionNotFound() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(platformRepository.findById(2L)).thenReturn(Optional.of(platform));
        when(regionRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> movieAvailabilityService.linkAvailability(1L, request));

        verify(regionRepository).findById(3L);
    }


    @Test
    void testLinkAvailability_AlreadyExists() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(platformRepository.findById(2L)).thenReturn(Optional.of(platform));
        when(regionRepository.findById(3L)).thenReturn(Optional.of(region));
        when(movieAvailabilityRepository.existsByMovieAndPlatformAndRegion(movie, platform, region)).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> movieAvailabilityService.linkAvailability(1L, request));

        verify(movieAvailabilityRepository, never()).save(any());
    }


    @Test
    void testUnlinkAvailability_Success() {
        when(movieAvailabilityRepository.findById(10L)).thenReturn(Optional.of(availability));

        movieAvailabilityService.unlinkAvailability(1L, 10L);

        verify(movieAvailabilityRepository).delete(availability);
    }

    @Test
    void testUnlinkAvailability_NotFound() {
        when(movieAvailabilityRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> movieAvailabilityService.unlinkAvailability(1L, 10L));

        verify(movieAvailabilityRepository).findById(10L);
    }



}
