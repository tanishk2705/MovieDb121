package com.CineTrust.dto;


import com.CineTrust.entity.PlatformType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlatformResponse {
    private Long id;
    private String name;
    private PlatformType type;
    private String website;
    private String createdAt;
    private String updatedAt;
}
