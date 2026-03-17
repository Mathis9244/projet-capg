package com.capg.hackathon.site.dto;

public record SiteComparisonResponse(
        SiteResponse siteA,
        SiteResponse siteB,
        double diffCo2TotalKg,
        double diffCo2ParM2,
        double diffCo2ParEmploye
) {
}

