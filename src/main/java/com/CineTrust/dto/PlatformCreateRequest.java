package com.CineTrust.dto;

import com.CineTrust.entity.PlatformType;
import com.CineTrust.validation.ValidWebsite;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlatformCreateRequest {
    @NotNull(message = "Platform name is required")
    @Size(max = 100, message = "Name can't exceed 100 characters")
    private String name;

    @NotNull(message = "Platform type is required")
    private PlatformType type;

    @ValidWebsite
    private String website;
}
