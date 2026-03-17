package com.capg.hackathon.site;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Site {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String localisation;

    private double surfaceM2;
    private int placesParking;
    private double consoEnergetiqueMWh;
    private int nbEmployes;

    private int annee;

    private double co2ConstructionKg;
    private double co2ExploitationKg;
    private double co2TotalKg;

    private Instant createdAt;

    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Material> materials;
}

