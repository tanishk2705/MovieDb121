package com.CineTrust.dto;

import com.CineTrust.entity.AvailabilityType;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieAvailabilityResponse {
    private Long id;
    private Long movieId;
    private String movieTitle;
    private String platformName;
    private String regionCode;
    private AvailabilityType availabilityType;
    private Date startDate;
    private Date endDate;
    private String url;
}
