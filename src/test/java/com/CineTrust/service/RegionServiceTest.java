package com.CineTrust.service;

import com.CineTrust.dto.RegionCreateRequest;
import com.CineTrust.dto.RegionResponse;
import com.CineTrust.entity.Region;
import com.CineTrust.exception.ResourceNotFoundException;
import com.CineTrust.repository.RegionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RegionServiceTest {

    @Mock
    private RegionRepository regionRepository;

    @InjectMocks
    private RegionService regionService;

    private Region region;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        region = Region.builder()
                .name("North America")
                .code("NA")
                .build();
        region.setId(1L);
        region.setCreatedAt(new Date());
        region.setUpdatedAt(new Date());
    }


    @Test
    void testCreateRegion_Success() {
        RegionCreateRequest request = new RegionCreateRequest();
        request.setName("North America");
        request.setCode("NA");

        when(regionRepository.existsByCodeIgnoreCase("NA")).thenReturn(false);
        when(regionRepository.save(any(Region.class))).thenReturn(region);

        RegionResponse response = regionService.createRegion(request);

        assertNotNull(response);
        assertEquals("North America", response.getName());
        assertEquals("NA", response.getCode());
        verify(regionRepository).save(any(Region.class));
    }


    @Test
    void testCreateRegion_AlreadyExists() {
        RegionCreateRequest request = new RegionCreateRequest();
        request.setName("North America");
        request.setCode("NA");

        when(regionRepository.existsByCodeIgnoreCase("NA")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> regionService.createRegion(request));

        verify(regionRepository, never()).save(any());
    }


    @Test
    void testGetRegionById_Success() {
        when(regionRepository.findById(1L)).thenReturn(Optional.of(region));

        RegionResponse response = regionService.getRegionById(1L);

        assertNotNull(response);
        assertEquals("North America", response.getName());
        assertEquals("NA", response.getCode());
        verify(regionRepository).findById(1L);
    }


    @Test
    void testGetRegionById_NotFound() {
        when(regionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> regionService.getRegionById(1L));

        verify(regionRepository).findById(1L);
    }
}
