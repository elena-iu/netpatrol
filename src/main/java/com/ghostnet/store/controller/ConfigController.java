package com.ghostnet.store.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller für Konfigurations-API
 * Stellt Konfigurationswerte wie API-Keys bereit
 */
@RestController
@RequestMapping("/api/config")
@PropertySource(value = "classpath:application-dev.properties", ignoreResourceNotFound = true)
public class ConfigController {

    @Value("${google.maps.api.key:}")
    private String googleMapsApiKey;

    /**
     * Liefert den Google Maps API Key
     * @return Map mit apiKey
     */
    @GetMapping("/maps-key")
    public ResponseEntity<Map<String, String>> getMapsApiKey() {
        Map<String, String> response = new HashMap<>();
        response.put("apiKey", googleMapsApiKey != null && !googleMapsApiKey.isEmpty() ? googleMapsApiKey : "");
        return ResponseEntity.ok(response);
    }
}

