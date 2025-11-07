package com.CineTrust.dto;


import com.CineTrust.entity.PlatformType;
import com.CineTrust.validation.ValidWebsite;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlatformUpdateRequest {

    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;

    private PlatformType type;

    @ValidWebsite
    private String website;
}
