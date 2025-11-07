package com.CineTrust.repository;


import com.CineTrust.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovieAvailabilityRepository extends JpaRepository<MovieAvailability, Long>, JpaSpecificationExecutor<MovieAvailability> {
    boolean existsByMovieAndPlatformAndRegion(Movie movie, Platform platform, Region region);
}
