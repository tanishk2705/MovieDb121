package com.CineTrust.dto;

import com.CineTrust.validation.ValidRegionCode;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegionCreateRequest {
    @NotBlank(message = "Region name is required")
    private String name;

    @ValidRegionCode
    @NotBlank(message = "Region code is required")
    private String code;
}
