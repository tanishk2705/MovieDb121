package com.CineTrust.service;

import com.CineTrust.dto.PlatformCreateRequest;
import com.CineTrust.dto.PlatformResponse;
import com.CineTrust.dto.PlatformUpdateRequest;
import com.CineTrust.entity.Platform;
import com.CineTrust.entity.PlatformType;
import com.CineTrust.exception.ResourceNotFoundException;
import com.CineTrust.repository.PlatformRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PlatformServiceTest {

    @Mock
    private PlatformRepository platformRepository;

    @InjectMocks
    private PlatformService platformService;

    private Platform platform;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        platform = Platform.builder()
                .name("Netflix")
                .type(PlatformType.OTT) // ✅ Correct enum value from your entity
                .website("https://www.netflix.com")
                .build();
        platform.setId(1L);
        platform.setCreatedAt(new Date());
        platform.setUpdatedAt(new Date());
    }

    @Test
    void testCreatePlatform_Success() {
        PlatformCreateRequest request = new PlatformCreateRequest();
        request.setName("Netflix");
        request.setType(PlatformType.OTT);
        request.setWebsite("https://www.netflix.com");

        when(platformRepository.existsByNameIgnoreCase("Netflix")).thenReturn(false);
        when(platformRepository.save(any(Platform.class))).thenReturn(platform);

        PlatformResponse response = platformService.createPlatform(request);

        assertNotNull(response);
        assertEquals("Netflix", response.getName());
        assertEquals(PlatformType.OTT, response.getType());
        verify(platformRepository).save(any(Platform.class));
    }


    @Test
    void testCreatePlatform_AlreadyExists() {
        PlatformCreateRequest request = new PlatformCreateRequest();
        request.setName("Netflix");
        request.setType(PlatformType.OTT);

        when(platformRepository.existsByNameIgnoreCase("Netflix")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> platformService.createPlatform(request));

        verify(platformRepository, never()).save(any());
    }


    @Test
    void testGetPlatformById_Success() {
        when(platformRepository.findById(1L)).thenReturn(Optional.of(platform));

        PlatformResponse response = platformService.getPlatformById(1L);

        assertNotNull(response);
        assertEquals("Netflix", response.getName());
        assertEquals(PlatformType.OTT, response.getType());
        verify(platformRepository).findById(1L);
    }


    @Test
    void testGetPlatformById_NotFound() {
        when(platformRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> platformService.getPlatformById(1L));

        verify(platformRepository).findById(1L);
    }

    @Test
    void testUpdatePlatform_Success() {
        PlatformUpdateRequest request = new PlatformUpdateRequest();
        request.setName("Prime Video");
        request.setType(PlatformType.OTT);
        request.setWebsite("https://www.primevideo.com");

        when(platformRepository.findById(1L)).thenReturn(Optional.of(platform));
        when(platformRepository.save(any(Platform.class))).thenReturn(platform);

        PlatformResponse response = platformService.updatePlatform(1L, request);

        assertNotNull(response);
        assertEquals("Prime Video", response.getName());
        assertEquals(PlatformType.OTT, response.getType());
        verify(platformRepository).save(any(Platform.class));
    }

    @Test
    void testUpdatePlatform_NotFound() {
        PlatformUpdateRequest request = new PlatformUpdateRequest();
        request.setName("Disney+Hotstar");

        when(platformRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> platformService.updatePlatform(1L, request));

        verify(platformRepository, never()).save(any());
    }


    @Test
    void testDeletePlatform_Success() {
        when(platformRepository.findById(1L)).thenReturn(Optional.of(platform));
        doNothing().when(platformRepository).delete(platform);

        platformService.deletePlatform(1L);

        verify(platformRepository).findById(1L);
        verify(platformRepository).delete(platform);
    }

    @Test
    void testDeletePlatform_NotFound() {
        when(platformRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> platformService.deletePlatform(1L));

        verify(platformRepository, never()).delete(any());
    }
}
