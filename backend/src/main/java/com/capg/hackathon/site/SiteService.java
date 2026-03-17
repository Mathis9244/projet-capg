package com.capg.hackathon.site;

import com.capg.hackathon.site.dto.SiteComparisonResponse;
import com.capg.hackathon.site.dto.SiteRequest;
import com.capg.hackathon.site.dto.SiteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Logique métier des sites : création (avec calcul CO₂ construction + exploitation),
 * liste, détail, comparaison. Utilise les facteurs d'émission en base (EmissionFactor).
 */
@Service
@RequiredArgsConstructor
public class SiteService {

    private final SiteRepository siteRepository;
    private final EmissionFactorRepository emissionFactorRepository;

    @Transactional
    public SiteResponse createSite(SiteRequest request) {
        Site site = new Site();
        site.setNom(request.nom());
        site.setLocalisation(request.localisation());
        site.setSurfaceM2(request.surfaceM2());
        site.setPlacesParking(request.placesParking());
        site.setConsoEnergetiqueMWh(request.consoEnergetiqueMWh());
        site.setNbEmployes(request.nbEmployes());
        site.setAnnee(request.annee());
        site.setCreatedAt(Instant.now());

        List<Material> materials = request.materials().stream()
                .map(m -> Material.builder()
                        .typeMateriau(m.typeMateriau())
                        .quantite(m.quantite())
                        .unite(m.unite())
                        .site(site)
                        .build())
                .toList();
        site.setMaterials(materials);

        double co2Construction = calculateConstructionEmissions(materials);
        double co2Exploitation = calculateOperationEmissions(request.consoEnergetiqueMWh());
        double total = co2Construction + co2Exploitation;

        site.setCo2ConstructionKg(co2Construction);
        site.setCo2ExploitationKg(co2Exploitation);
        site.setCo2TotalKg(total);

        Site saved = siteRepository.save(site);
        return toResponse(saved);
    }

    public List<SiteResponse> listSites() {
        return siteRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public SiteResponse getSite(Long id) {
        return siteRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Site not found"));
    }

    public SiteComparisonResponse compare(Long idA, Long idB) {
        SiteResponse a = getSite(idA);
        SiteResponse b = getSite(idB);

        double diffTotal = b.co2TotalKg() - a.co2TotalKg();
        double diffParM2 = b.co2ParM2() - a.co2ParM2();
        double diffParEmploye = b.co2ParEmploye() - a.co2ParEmploye();

        return new SiteComparisonResponse(a, b, diffTotal, diffParM2, diffParEmploye);
    }

    private double calculateConstructionEmissions(List<Material> materials) {
        return materials.stream()
                .mapToDouble(m -> {
                    String code = "MATERIAL_" + m.getTypeMateriau().toUpperCase();
                    double factor = emissionFactorRepository.findByCode(code)
                            .map(EmissionFactor::getFacteurKgCO2ParUnite)
                            .orElse(0.0);
                    return m.getQuantite() * factor;
                })
                .sum();
    }

    private double calculateOperationEmissions(double consoEnergetiqueMWh) {
        double factor = emissionFactorRepository.findByCode("ENERGY_ELECTRICITY")
                .map(EmissionFactor::getFacteurKgCO2ParUnite)
                .orElse(0.0);
        return consoEnergetiqueMWh * factor;
    }

    private SiteResponse toResponse(Site s) {
        double co2ParM2 = s.getSurfaceM2() > 0 ? s.getCo2TotalKg() / s.getSurfaceM2() : 0;
        double co2ParEmploye = s.getNbEmployes() > 0 ? s.getCo2TotalKg() / s.getNbEmployes() : 0;

        return new SiteResponse(
                s.getId(),
                s.getNom(),
                s.getLocalisation(),
                s.getSurfaceM2(),
                s.getPlacesParking(),
                s.getConsoEnergetiqueMWh(),
                s.getNbEmployes(),
                s.getAnnee(),
                s.getCo2ConstructionKg(),
                s.getCo2ExploitationKg(),
                s.getCo2TotalKg(),
                co2ParM2,
                co2ParEmploye,
                s.getCreatedAt(),
                s.getMaterials().stream()
                        .map(m -> new SiteResponse.MaterialDto(
                                m.getId(),
                                m.getTypeMateriau(),
                                m.getQuantite(),
                                m.getUnite()))
                        .toList()
        );
    }
}

