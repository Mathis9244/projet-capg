package com.capg.hackathon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entrée du backend "Empreinte Carbone" — Hackathon 2026 Capgemini.
 * API REST (sites, auth), JWT, calcul CO₂ construction/exploitation, persistance (JPA).
 */
@SpringBootApplication
public class CarbonAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarbonAppApplication.class, args);
    }
}

