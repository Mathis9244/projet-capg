package com.capg.hackathon.site.dto;

import java.time.Instant;
import java.util.List;

public record SiteResponse(
        Long id,
        String nom,
        String localisation,
        double surfaceM2,
        int placesParking,
        double consoEnergetiqueMWh,
        int nbEmployes,
        int annee,
        double co2ConstructionKg,
        double co2ExploitationKg,
        double co2TotalKg,
        double co2ParM2,
        double co2ParEmploye,
        Instant createdAt,
        List<MaterialDto> materials
) {
    public record MaterialDto(
            Long id,
            String typeMateriau,
            double quantite,
            String unite
    ) {
    }
}

