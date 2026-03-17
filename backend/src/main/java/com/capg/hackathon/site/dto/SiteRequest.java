package com.capg.hackathon.site.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record SiteRequest(
        @NotBlank String nom,
        @NotBlank String localisation,
        @Positive double surfaceM2,
        @Min(0) int placesParking,
        @Positive double consoEnergetiqueMWh,
        @Min(0) int nbEmployes,
        @Positive int annee,
        List<MaterialDto> materials
) {

    public record MaterialDto(
            @NotBlank String typeMateriau,
            @Positive double quantite,
            @NotBlank String unite
    ) {
    }
}

