package com.ghostnet.store.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

/**
 * Controller für vollständige Seiten
 * Stellt das Layout-Template für verschiedene Routen bereit
 * Ermöglicht direktes Laden der Seiten ohne JavaScript-Navigation
 */
@SuppressWarnings("null")
@Controller
public class PageController {

    /**
     * Zeigt "Meine Netze" Seite mit Layout an
     * @return layout.html Template
     */
    @GetMapping("/meine-netze")
    public ResponseEntity<Resource> meineNetze() throws IOException {
        Resource resource = new ClassPathResource("templates/layout.html");
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(resource);
    }

    /**
     * Zeigt "Meldungen" Seite mit Layout an
     * @return layout.html Template
     */
    @GetMapping("/meldungen")
    public ResponseEntity<Resource> meldungen() throws IOException {
        Resource resource = new ClassPathResource("templates/layout.html");
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(resource);
    }

    /**
     * Zeigt "Bergungen" Seite mit Layout an
     * @return layout.html Template
     */
    @GetMapping("/bergungen")
    public ResponseEntity<Resource> bergungen() throws IOException {
        Resource resource = new ClassPathResource("templates/layout.html");
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(resource);
    }
}

