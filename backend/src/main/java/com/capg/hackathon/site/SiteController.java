package com.capg.hackathon.site;

import com.capg.hackathon.site.dto.SiteComparisonResponse;
import com.capg.hackathon.site.dto.SiteRequest;
import com.capg.hackathon.site.dto.SiteResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * API REST des sites : POST (création + calcul), GET (liste, détail, comparaison).
 * Toutes les routes nécessitent une authentification JWT (sauf /api/auth/*).
 */
@RestController
@RequestMapping("/api/sites")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SiteController {

    private final SiteService siteService;

    @PostMapping
    public ResponseEntity<SiteResponse> create(@Valid @RequestBody SiteRequest request) {
        return ResponseEntity.ok(siteService.createSite(request));
    }

    @GetMapping
    public List<SiteResponse> list() {
        return siteService.listSites();
    }

    @GetMapping("/{id}")
    public SiteResponse get(@PathVariable Long id) {
        return siteService.getSite(id);
    }

    @GetMapping("/compare")
    public SiteComparisonResponse compare(
            @RequestParam Long siteA,
            @RequestParam Long siteB
    ) {
        return siteService.compare(siteA, siteB);
    }
}

