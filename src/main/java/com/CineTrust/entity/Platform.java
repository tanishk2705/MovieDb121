package com.CineTrust.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "platforms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Platform extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 50)
    @Enumerated(EnumType.STRING)
    private PlatformType type;

    @Column(columnDefinition = "TEXT")
    private String website;


}
