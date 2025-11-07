package com.CineTrust.repository;


import com.CineTrust.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionRepository extends JpaRepository<Region, Long> {
    boolean existsByCodeIgnoreCase(String code);
}
