package com.ghostnet.store.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

/**
 * Controller für das Dashboard
 * Stellt das Layout-Template für authentifizierte Bereiche bereit
 */
@SuppressWarnings("null")
@Controller
public class DashboardController {

    /**
     * Zeigt das Dashboard mit Layout an
     * @return layout.html Template
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Resource> dashboard() throws IOException {
        Resource resource = new ClassPathResource("templates/layout.html");
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(resource);
    }

    /**
     * Liefert nur den Dashboard-Content (ohne Layout)
     * @return dashboard.html Content
     */
    @GetMapping("/dashboard.html")
    public ResponseEntity<Resource> dashboardContent() throws IOException {
        Resource resource = new ClassPathResource("templates/content/dashboard.html");
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(resource);
    }
}

