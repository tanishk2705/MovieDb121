package com.CineTrust.dto;

import com.CineTrust.entity.AvailabilityType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieAvailabilityRequest {

    @NotNull(message = "Platform ID is required")
    private Long platformId;

    @NotNull(message = "Region ID is required")
    private Long regionId;

    @NotNull(message = "Availability type is required")
    private AvailabilityType availabilityType;

    private Date startDate;
    private Date endDate;
    private String url;
}