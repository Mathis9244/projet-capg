package com.capg.hackathon.config;

import com.capg.hackathon.site.EmissionFactor;
import com.capg.hackathon.site.EmissionFactorRepository;
import com.capg.hackathon.user.Role;
import com.capg.hackathon.user.User;
import com.capg.hackathon.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Données initiales au démarrage : utilisateur admin (admin/admin) et facteurs
 * d'émission par défaut (matériaux + énergie). À remplacer par des valeurs ADEME/OpenData en production.
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmissionFactorRepository emissionFactorRepository;

    @Override
    public void run(ApplicationArguments args) {
        seedAdmin();
        seedEmissionFactors();
    }

    private void seedAdmin() {
        if (userRepository.findByUsername("admin").isPresent()) {
            return;
        }
        userRepository.save(User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin"))
                .role(Role.ADMIN)
                .build());
    }

    private void seedEmissionFactors() {
        // Valeurs par défaut (mock). À remplacer par ADEME / OpenData ensuite.
        Map<String, Double> factors = Map.of(
                "ENERGY_ELECTRICITY", 55.0,  // kgCO2e par MWh (valeur illustrative)
                "MATERIAL_BETON", 120.0,     // kgCO2e par tonne
                "MATERIAL_ACIER", 1900.0,    // kgCO2e par tonne
                "MATERIAL_VERRE", 900.0,     // kgCO2e par tonne
                "MATERIAL_BOIS", 50.0        // kgCO2e par tonne
        );

        for (var entry : factors.entrySet()) {
            emissionFactorRepository.findByCode(entry.getKey())
                    .orElseGet(() -> emissionFactorRepository.save(
                            EmissionFactor.builder()
                                    .code(entry.getKey())
                                    .facteurKgCO2ParUnite(entry.getValue())
                                    .build()
                    ));
        }
    }
}

