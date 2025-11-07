package com.CineTrust.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegionResponse {
    private Long id;
    private String name;
    private String code;
    private String createdAt;
    private String updatedAt;
}

