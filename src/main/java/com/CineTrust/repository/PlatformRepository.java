package com.CineTrust.repository;

import com.CineTrust.entity.Platform;
import com.CineTrust.entity.PlatformType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlatformRepository extends JpaRepository<Platform, Long> {
    boolean existsByNameIgnoreCase(String name);

    Page<Platform> findByNameContainingIgnoreCaseAndType(String name, PlatformType type, Pageable pageable);

    Page<Platform> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Platform> findByType(PlatformType type, Pageable pageable);

}
