package com.ghostnet.store.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

/**
 * Controller für Content-Fragmente
 * Stellt einzelne Content-Templates für dynamisches Laden bereit
 */
@SuppressWarnings("null")
@Controller
public class ContentController {

    /**
     * Liefert den Content für "Meine Netze"
     * @return meine-netze.html Content
     */
    @GetMapping("/meine-netze.html")
    public ResponseEntity<Resource> meineNetze() throws IOException {
        Resource resource = new ClassPathResource("templates/content/meine-netze.html");
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(resource);
    }

    /**
     * Liefert den Content für "Meldungen"
     * @return meldungen.html Content
     */
    @GetMapping("/meldungen.html")
    public ResponseEntity<Resource> meldungen() throws IOException {
        Resource resource = new ClassPathResource("templates/content/meldungen.html");
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(resource);
    }

    /**
     * Liefert den Content für "Bergungen"
     * @return bergungen.html Content
     */
    @GetMapping("/bergungen.html")
    public ResponseEntity<Resource> bergungen() throws IOException {
        Resource resource = new ClassPathResource("templates/content/bergungen.html");
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(resource);
    }

}

